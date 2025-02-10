package com.lazylite.media.remote.core.ijkwrapper;

import android.text.TextUtils;
import android.util.Log;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.bean.PlayLogInfo;
import com.lazylite.media.playctrl.PlayProxy;
import com.lazylite.media.remote.AIDLPlayDelegate;
import com.lazylite.media.remote.core.down.DownCacheMgr;
import com.lazylite.media.remote.core.down.DownloadDelegate;
import com.lazylite.media.remote.core.play.PlayFileProxy;
import com.lazylite.media.utils.MediaDirs;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.mod.utils.KwFileUtils;
import com.lazylite.mod.utils.KwTimer;

import org.ijkplayer.IMediaPlayer;

import java.io.IOException;
import java.util.Arrays;

import cn.kuwo.p2p.FileServerJNI;
import cn.kuwo.service.remote.kwplayer.Spectrum;

public class IjkPlayerPlayCtrl implements IPlayCtrl, KwTimer.Listener, Spectrum {
	private static final String TAG = IjkPlayerPlayCtrl.class.getCanonicalName();
	private KwIjkPlayer mIjkPlayer;
	private ThreadMessageHandler mMsgHandler;
	private AIDLPlayDelegate mDelegate;
	private PlayStateNotify mPlayStateNotify;
	private AudioInfo audioInfo;

	private boolean mIsPlayLocal = false;
	private boolean mIsRadio;

	private PlayDelegate.PlayContent mPlayContent = PlayDelegate.PlayContent.MUSIC;
	private boolean mIsStopedCD = false;//保证CD包播放Stop通知只调用一次
	private int mPlayEndTime = -1;//CD播放结束时间

	private int 	mDownloadedLen = 0;
    private int 	mTotalFileLen = 0;
    private int		musicBitrate;

    private String mSavePath;
	private String musicFormatStr;
	private static String mLastNeedDelete;
	
	private DownloadDelegate.DataSrc mDataSrc;
	private int 	mBufferingPercent = 0;
	private int 	mAverageSpeed;
	private long 	mStartDownloadTime;
    private long 	mBufferingStartTime;
	protected volatile PlayProxy.Status	mStatus				= PlayProxy.Status.INIT;
	private KwTimer mTimer = null;
	private int mContinuePos = 0;
	private static final long	START_BUFFER_TIME_OUT	= 30 * 1000;		// 准备播放前缓冲数据多久缓冲进度不变算超时
	private static final long	WAITING_BUFFER_TIME_OUT	= 30 * 1000;
	private static final long	WAITING_DELEGATESTART_TIME_OUT	= 30 * 1000;	//调用playNet到p2p代理调用DownloadDelegate_Start开始播放超时，代理不调用时防止此处卡死

	//是否开启频谱数据
	private boolean fftReceiveEnable;
	//是否开启频谱显示
	private boolean spectrumEnable;

	private IjkPlayerCallback playCallBack = new PlayCallback();

	public IjkPlayerPlayCtrl() {
		mPlayStateNotify = new PlayStateNotify();
	}

	private void startTimer(){     
    	if (mTimer == null) {
			mTimer = new KwTimer(this);
		}
		mTimer.setListener(this);
		mTimer.start(1000);	
    }

	private void stopTimer() {
		if (mTimer != null) {
			mTimer.stop();
		}
	}
	
	@Override
	public void onTimer(KwTimer timer) {
		long currentTime = System.currentTimeMillis();
		if(mIjkPlayer == null) {
			if(timer.getRunningTimeMiliseconds() > WAITING_DELEGATESTART_TIME_OUT) {
				Log.w(TAG, "DownloadDelegate_Start timeout cost: " + timer.getRunningTimeMiliseconds());
				stopTimer();

                mPlayStateNotify.notifyError(PlayDelegate.ErrorCode.NETWORK_ERROR_OOT_START, 
                		PlayFileProxy.getInstance().getLastError());
			}
			return;
		}
		
		if((KwIjkPlayer.STATE_BUFFERING == mIjkPlayer.getPlayerState()) && !mIsPlayLocal) {
			if(mBufferingStartTime <= 0)
				return;
			long cost = currentTime - mBufferingStartTime;
			if (cost >= START_BUFFER_TIME_OUT){
				String str = "playing buffer timeout";
                Log.i(TAG, str + " cost:" + cost);
				stopTimer();
                mPlayStateNotify.notifyError(PlayDelegate.ErrorCode.NETWORK_ERROR_OOT_BUFFER, str);
            }
		} else if( KwIjkPlayer.STATE_PLAYING == mIjkPlayer.getPlayerState()) {
			//播放控制必须主动通知播放进度,PlayMusicImpl类会统计播放进度；本地和网络都需要发送
			mPlayStateNotify.notifyPlayProgress(getDuration(), getCurrentPos(), 0);
		}

	}

	@Override
	public void setDelegate(AIDLPlayDelegate delegate) {
		Log.i(TAG, "setDelegate");
		if (mPlayStateNotify != null){
        	mPlayStateNotify.setDelegate(delegate);
        }
        else {
            Log.e(TAG, "mPlayStateNotify is null");
        }
	}



	protected void init() {
		musicFormatStr 	= null;
		musicBitrate 	= 0;

		mDownloadedLen = 0;
		mTotalFileLen = 0;
		mAverageSpeed = 0;
		musicBitrate = 0;
		mIsStopedCD = false;
		audioInfo = null;

	}
	@Override
	public PlayDelegate.ErrorCode playLocal(String filePath, boolean isRadio, PlayDelegate.PlayContent playContent, int continuePos) {
		Log.i(TAG, "playLocal:" + filePath);
		if (TextUtils.isEmpty(filePath)) {
			return PlayDelegate.ErrorCode.FILENOTEXIST;
		}

		//停止上一首歌
		finish();

		init();
		startTimer();
		mPlayContent = playContent;
		mIsPlayLocal = true;
		mIsRadio = isRadio;
		mDownloadedLen = mTotalFileLen = 100;
		mSavePath = filePath;
		mContinuePos = continuePos;

        if (mPlayStateNotify != null){
            mPlayStateNotify.incVersion();
        }

		musicFormatStr = DownCacheMgr.getSongFormat(filePath);
		PlayFileProxy.getInstance().init(mMsgHandler);
		PlayFileProxy.getInstance().cancel();

		try {
			ijkPlayMusic(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}


		return PlayDelegate.ErrorCode.SUCCESS;
	}

	@Override
	public PlayDelegate.ErrorCode playLocal(AudioInfo audioInfo, int continuePos) {
		String filePath = audioInfo.mFilePath;
		Log.i(TAG, "playLocal:" + filePath);
		if (TextUtils.isEmpty(filePath)) {
			return PlayDelegate.ErrorCode.FILENOTEXIST;
		}
		//停止上一首歌
		finish();
		init();
		startTimer();
		mPlayContent = PlayDelegate.PlayContent.TINGSHU;
		mIsPlayLocal = true;
		mIsRadio = false;
		mDownloadedLen = mTotalFileLen = 100;
		mSavePath = filePath;
		mContinuePos = continuePos;
		this.audioInfo = audioInfo;

		if (mPlayStateNotify != null){
			mPlayStateNotify.incVersion();
		}
		musicFormatStr = DownCacheMgr.getSongFormat(filePath);
		PlayFileProxy.getInstance().init(mMsgHandler);

		if (FileServerJNI.isKwmPocoFile(filePath)) {
			filePath = PlayFileProxy.getInstance().startLocal(filePath, 1, null);
			if (filePath == null) {
				return PlayDelegate.ErrorCode.NO_HTTP_URL;
			}
		}

		try {
			ijkPlayMusic(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return PlayDelegate.ErrorCode.SUCCESS;
	}

	@Override
	public PlayDelegate.ErrorCode playNet(AudioInfo audioInfo, int continuePos) {
		//停止上一首歌
		finish();
		init();

		mPlayContent = PlayDelegate.PlayContent.TINGSHU;
		mIsRadio = false;
		mIsPlayLocal = false;
		mContinuePos = continuePos;
		this.audioInfo = audioInfo;
		KwWifiLock.lock();
		if (mPlayStateNotify != null){
			mPlayStateNotify.incVersion();
		}
		PlayFileProxy.getInstance().init(mMsgHandler);
		PlayFileProxy.getInstance().startNet(audioInfo, downloadDelegate);

		startTimer();
		return PlayDelegate.ErrorCode.SUCCESS;
	}

	@Override
	public void pause() {
		if(mIjkPlayer != null )
		{
			mIjkPlayer.pause();
			mPlayStateNotify.notifyPause(PlayProxy.Status.PAUSE);
		}
	}

	@Override
	public void resume() {
		if(mIjkPlayer != null )
		{
			mIjkPlayer.start();
			mPlayStateNotify.notifyResume(PlayProxy.Status.PLAYING);
		}
	}

	@Override
	public void stop() {
		//暂时注释，保持和线上版逻辑一致
		//PlayFileProxy.getInstance().removeTask();
		finish();
	}

	private void finish() {
		stopTimer();
		if(mIjkPlayer != null )
			mIjkPlayer.stop();
		mIjkPlayer = null;
		audioInfo = null;
	}

	@Override
	public PlayProxy.Status getStatus() {
		 // Log.i(TAG, "getStatus");
		if(mIjkPlayer == null)
			return PlayProxy.Status.INIT;;
	    int state = mIjkPlayer.getPlayerState();
	       
	    switch(state)
	    {
	    	case KwIjkPlayer.STATE_PREPARING:
	        case KwIjkPlayer.STATE_PREPARED:
	        	mStatus = PlayProxy.Status.INIT;
	            break;
	        case KwIjkPlayer.STATE_BUFFERING:
	        	mStatus = PlayProxy.Status.BUFFERING;
	            break;
	        case KwIjkPlayer.STATE_PLAYING:
	        	mStatus = PlayProxy.Status.PLAYING;
	        	break;
	        case KwIjkPlayer.STATE_PAUSED:
	            mStatus = PlayProxy.Status.PAUSE;
	            break;
	        case KwIjkPlayer.STATE_PLAYBACK_COMPLETED:
	        case KwIjkPlayer.STATE_ERROR:
	            mStatus = PlayProxy.Status.STOP;
	            break;
	        default:
	            mStatus = PlayProxy.Status.INIT;
	            break;
	    }

//	    Log.i(TAG, "ijkplayer state=" + state + "status=" + mStatus);
	    return mStatus;
	}

	@Override
	public int getDuration() {
		if(mIjkPlayer == null)
			return 0;
		if (getStatus() == PlayProxy.Status.INIT ||
				(getStatus() == PlayProxy.Status.STOP)) {
			return 0;
		}
		if (audioInfo != null && audioInfo.serverDuration > 0) {
			return audioInfo.serverDuration * 1000;
		}
		return (int)mIjkPlayer.getDuration();
	}

	@Override
	public int getCurrentPos() {
		if(mIjkPlayer == null)
			return 0;
		if ((getStatus() == PlayProxy.Status.INIT )||
				(getStatus() == PlayProxy.Status.STOP))
			return 0;

		return (int)mIjkPlayer.getCurrentPosition();
	}

	@Override
	/**
	 * 获取已下载的缓冲时间
	 */
	public int getBufferPos() {
		if (getStatus() == PlayProxy.Status.INIT) {
			return 0;
		}
		
		int totalTime = getDuration();
		final int MAX_EXTRA_SIZE = 30720;
		
		if(mIsPlayLocal)
			return totalTime;
		
		if (mDownloadedLen == 0 || mDownloadedLen < MAX_EXTRA_SIZE || totalTime == 0 || mTotalFileLen == 0) {
			return 0;
		}

        float bufferPercent;
        if (mTotalFileLen < MAX_EXTRA_SIZE) {
            bufferPercent =(float) mDownloadedLen / (float)mTotalFileLen;      
        } else {
            bufferPercent =(float) (mDownloadedLen - MAX_EXTRA_SIZE) / (float)(mTotalFileLen - MAX_EXTRA_SIZE);
        }

        int bufferTime = (int)(totalTime * bufferPercent);
        
		return bufferTime;
		
	}

	@Override
	public int getPreparingPercent() {
		//Log.i(TAG, "getPreparingPercent");
		return mBufferingPercent;
	}

	final int	BUFFER_SEEK_MILLISECOND		= 10000;
	@Override
	public void seek(int pos) {
		if(mIjkPlayer == null)
			return;

		if ( (getStatus() != PlayProxy.Status.PLAYING) && (getStatus() != PlayProxy.Status.PAUSE))
			return;

		if (isDownComplete() || getBufferPos() - BUFFER_SEEK_MILLISECOND > pos){
			if(pos > (getDuration() - 500))
			{//send playstop when seek too near(500ms) end
				mPlayStateNotify.notifyStop(mSavePath, true, mPlayContent);
				return;
			}
            mIjkPlayer.seekTo(pos);
			mPlayStateNotify.notifySeekSuccess(pos, mPlayContent);
		} else {
            Log.i(TAG, "can't seek need buffer");
        }
	}

	@Override
	public boolean getPlayLogInfo(PlayLogInfo info) {
		if (getStatus() == PlayProxy.Status.INIT || mPlayContent == PlayDelegate.PlayContent.KSING) {
			return false;
		}

		info.format 		= musicFormatStr;
		info.bitrate 		= mIsPlayLocal ? 0 : musicBitrate;
		info.download 		= mIsPlayLocal ? false : (mDataSrc != DownloadDelegate.DataSrc.LOCAL_FULL);
		info.averageSpeed 	= mIsPlayLocal ? 0 : mAverageSpeed;
		info.fileSize 		= mTotalFileLen;

		return true;
	}


	@Override
	public void setEndTime(int endTime) {
		this.mPlayEndTime = endTime;
	}

	@Override
	public void setSpeed(float speed) {
		if(mIjkPlayer != null ){
			mIjkPlayer.setSpeed(speed);
		}
	}

	@Override
	public float getSpeed() {
		if (mIjkPlayer!=null){
			return mIjkPlayer.getSpeed();
		}
		return .0f;
	}

    
	private boolean isDownComplete() {
		return mTotalFileLen>0 && mDownloadedLen == mTotalFileLen;
	}
	
    public void setMessageHandler(ThreadMessageHandler msgHandler) { 
        Log.i(TAG, "setMessageHandler");
		this.mMsgHandler = msgHandler;     
	}
	
	private void doDeleteWhenNext(final String currentPath) {
		if (mLastNeedDelete == null) {
			return;
		}
		if (mLastNeedDelete.equals(currentPath)) { // 再次播同一首歌，不删
			return;
		}
		if (!KwFileUtils.isExist(mLastNeedDelete)) {
			mLastNeedDelete = null;
			return;
		}
		DownCacheMgr.deleteCacheFile(mLastNeedDelete);
		mLastNeedDelete = null;
	}
	
	private boolean isCacheFile(final String path) {
		String cacheDir = MediaDirs.getMediaPath(MediaDirs.PLAY_CACHE);
		return path.startsWith(cacheDir);
	}
	
	private void rememberNeedDeleteWhenNext(final String path) {
		mLastNeedDelete = null;
		//TODO 缓存文件
		if (mPlayContent == PlayDelegate.PlayContent.TINGSHU) {
			mLastNeedDelete = path;
			return;
		}

	}
	
	private PlayDelegate.ErrorCode downErr2playErr(DownloadDelegate.ErrorCode downErr) {
		switch (downErr) { // 之所以用switch而不用bitmap是为了DownErr增加新类型时候能够报错免得漏掉
		case SUCCESS:
			return PlayDelegate.ErrorCode.SUCCESS;
		case ANTISTEALING_FAILED:
			return PlayDelegate.ErrorCode.NETWORK_ERROR_ANTISTEALING;
		case NO_NET:
			return PlayDelegate.ErrorCode.NO_NETWORK;
		case NET_ERROR:
			return PlayDelegate.ErrorCode.NETWORK_ERROR_DOWNERR;
		case NET_CDN_ERROR:
			return PlayDelegate.ErrorCode.NETWORK_ERROR_CDNERR;
		case IO_ERROR:
			return PlayDelegate.ErrorCode.IO_ERROR;
		case NO_SDCARD:
			return PlayDelegate.ErrorCode.NO_SDCARD;
		case NOSPACE:
			return PlayDelegate.ErrorCode.NO_SPACE;
		case ONLYWIFI:
			return PlayDelegate.ErrorCode.ONLYWIFI;
		case OTHERS:
			return PlayDelegate.ErrorCode.OTHERDOWNERR;
		}
		return PlayDelegate.ErrorCode.OTHERDOWNERR;
	}

    private DownloadDelegate downloadDelegate = new DownloadDelegate() {

		@Override
		public void DownloadDelegate_Start(int id, String url, String tempPath,
                                           int totalLen, int currentLen, int bitrate, DataSrc dataSrc) {
			Log.i(TAG, "down start tempPath=" + tempPath);
			if (url == null){
				mPlayStateNotify.notifyError(PlayDelegate.ErrorCode.NO_HTTP_URL, "DownloadDelegate_Start URL is null!");
				stopTimer();
				return;
			}
			if(dataSrc == DataSrc.LOCAL_FULL) {
				if(FileServerJNI.isKwmPocoFile(tempPath))
					FileServerJNI.setEncrypt(tempPath, 1);
			}
			musicFormatStr = DownCacheMgr.getSongFormat(url);
			if (TextUtils.isEmpty(musicFormatStr)) {
				//KwDebug.classicAssert(false, musicFormatStr + " " + url);
				musicFormatStr = "aac";
			}
			
			musicBitrate	= bitrate;
            mDownloadedLen 	= currentLen;
            mTotalFileLen 	= totalLen;
            mSavePath 		= null;
			mDataSrc		=dataSrc;
			mStartDownloadTime = System.currentTimeMillis();

			doDeleteWhenNext(tempPath); // 如果上次播放有需要删除的电台/缓存文件的话，删掉
			rememberNeedDeleteWhenNext(tempPath); // 看本次是播放是否需要在下一首时候删除
       
			try {
				ijkPlayMusic(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void DownloadDelegate_Progress(int id, int totalLen,
				int current, float speed) {
			if (mDownloadedLen < current){
                mDownloadedLen = current;
            }
		    Log.i(TAG, "down progress, current=" + current + "totalLen=" + totalLen);
		    
		    int t = (int) (System.currentTimeMillis() - mStartDownloadTime);
			if (t > 0) {
				mAverageSpeed = current / t * 1000;
			}
			mPlayStateNotify.notifyCacheProgress(current, totalLen);
		}

		@Override
		public void DownloadDelegate_Finish(int id, ErrorCode err,
				String savePath) {
			if (err == DownloadDelegate.ErrorCode.SUCCESS) {
				//Log.i(TAG, "down finish, success=" + err + "savePath=" + savePath);
				int t = (int) (System.currentTimeMillis() - mStartDownloadTime);
				if (t > 0) {
					mAverageSpeed = mTotalFileLen / t * 1000;
				}
				mDownloadedLen = mTotalFileLen;
				mSavePath = savePath;
				if (audioInfo != null) {
					mPlayStateNotify.notifyDownloadFinished(getStatus(), savePath, audioInfo.rid);
				}else {
					mPlayStateNotify.notifyDownloadFinished(getStatus(), savePath, 0);
				}
				//mPlayStateNotify.notifyError(PlayDelegate.ErrorCode.SUCCESS);
				rememberNeedDeleteWhenNext(savePath); // 看本次是播放是否需要在下一首时候删除
			}
			else {
				stopTimer();
				mPlayStateNotify.notifyError(downErr2playErr(err), "DownloadDelegate_Finish : error!");
			}
		}
    	
    };
    
	public boolean ijkPlayMusic(String path) throws IOException
	{
		if(mIjkPlayer != null) {
			mIjkPlayer.playNext(path);
		} else {
			mIjkPlayer = new KwIjkPlayer();
			mIjkPlayer.setDataSource(path);
			if(mContinuePos > 0)
				mIjkPlayer.prepareAsync((long)mContinuePos);
			else
				mIjkPlayer.prepareAsync();
		}
		mIjkPlayer.setSpectrum(fftReceiveEnable, this);//是否开启频谱
		mIjkPlayer.setCallBack(playCallBack); //这里经常调用，不适合每次都new一个callBack对象
		return true;
	}

	//底层数据设置开关，只会在切歌时设置
	public void setFFTDataEnableReceive(boolean enable) {
		fftReceiveEnable=enable;
	}

	//上层数据传递开关
	public void setSpectrumEnable(boolean enable) {
		spectrumEnable = enable;
	}


	float[] model_left;
	float[] model_right;
	@Override
	public void onFFXData(double[] left, double[] right) {
		if (!fftReceiveEnable || !spectrumEnable) {
			return;
		}
		//Log.d("FFTUpdate","-------onFFXData start-----====");
		double tmpValue;
		if(model_left==null){
			model_left = new float[left.length / 2];  //1024的入参数数组，转为分贝值，只有512个数
		}else{
			Arrays.fill(model_left,0); //内存分配太慢，这里只全局保存一个，复用
		}
		for (int i = 0, j = 0; j < left.length / 2; ) {
			tmpValue = Math.hypot(left[i], left[i + 1]); //参数数据中数据，每二个数是一个实部，一个虚部，这里把这对数转为一个值存储。公式是等价于 sqrt(x*x,y*y);
			model_left[j] = (float) Math.log10(1.0f + tmpValue); //以10为底取对数后，此值转为分贝值
			i += 2;
			j++;
		}
		if(model_right==null){
			model_right = new float[right.length / 2];
		}else{
			Arrays.fill(model_right,0);
		}
		for (int i = 0, j = 0; j < right.length / 2; ) {
			tmpValue = Math.hypot(right[i], right[i + 1]);
			model_right[j] = (float) Math.log10(1.0f + tmpValue);
			i += 2;
			j++;
		}

		//Log.d("FFTUpdate","-------onFFXData end-----====");
		if (mPlayStateNotify != null) {
			mPlayStateNotify.notifyFFTDataReceive(model_left, model_right);
		}
	}

	private class PlayCallback implements IjkPlayerCallback {

		@Override
		public void onStartPlaying() {
			mPlayStateNotify.notifyStart(PlayProxy.Status.PLAYING, 0);			
		}
		
		@Override
		public void onStartBuffering() {
			mBufferingStartTime = System.currentTimeMillis();
			//播放本地歌曲不显示缓冲信息
			if(mIsPlayLocal)
				return;
			mPlayStateNotify.notifyBuffering(PlayProxy.Status.BUFFERING);
		}

		@Override
		public void onEndBuffering() {
			//reset mBufferingStartTime
			mBufferingStartTime = 0;
			if(mIsPlayLocal)
				return;
			mPlayStateNotify.notifyBufferingFinish();
		}
 
		@Override
		public void onBuffering(float cachePercent) {
			Log.i(TAG, "cachePercent = " + cachePercent);
			mBufferingPercent = (int)cachePercent;
		}

		@Override
		public void onProgress(int nProgress) {

		}

		@Override
		public void onPlayerPaused() {

		}

		@Override
		public void onPlayerStopped() {
			mPlayStateNotify.notifyStop(mSavePath, true , mPlayContent);
		}

		@Override
		public void onEncounteredError(int err) {
			switch(err)
			{
			case IMediaPlayer.MEDIA_ERROR_EIO:
				mPlayStateNotify.notifyError(PlayDelegate.ErrorCode.IO_ERROR, "ijkplayer io error!");
				break;
			case IMediaPlayer.MEDIA_ERROR_ENOENT:
				mPlayStateNotify.notifyError(PlayDelegate.ErrorCode.FILENOTEXIST, "ijkplayer error \"No such file or directory\"!!!");
				break;
			case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
			case IMediaPlayer.MEDIA_ERROR_CONNECTION_REFUSED:
			case IMediaPlayer.MEDIA_ERROR_NETWORK_UNREACHABLE:
				mPlayStateNotify.notifyError(PlayDelegate.ErrorCode.NO_NETWORK, "ijkplayer network error!");
				break;
			default :
				String str = "ijkplayer play 《" + mIjkPlayer.getDataSource() + "》error : " + err;
				mPlayStateNotify.notifyError(PlayDelegate.ErrorCode.DECODE_FAILE, str);
				break;
			}
		}

		@Override
		public void onSeekComplete() {
			
		}

   	
    }
 
}
