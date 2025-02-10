package com.lazylite.media.remote.core.ijkwrapper;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import com.lazylite.media.Media;
import com.lazylite.media.utils.MediaDirs;
import com.lazylite.mod.utils.KwFileUtils;

import org.ijkplayer.IMediaPlayer;
import org.ijkplayer.IMediaPlayer.OnBufferingUpdateListener;
import org.ijkplayer.IMediaPlayer.OnCompletionListener;
import org.ijkplayer.IMediaPlayer.OnErrorListener;
import org.ijkplayer.IMediaPlayer.OnInfoListener;
import org.ijkplayer.IMediaPlayer.OnPreparedListener;
import org.ijkplayer.IMediaPlayer.OnSeekCompleteListener;
import org.ijkplayer.IjkMediaPlayer;

import java.io.IOException;

import cn.kuwo.service.remote.kwplayer.Spectrum;

/**
 * Created by Administrator on 2016/3/9.
 */
public class KwIjkPlayer {
    private final String TAG = KwIjkPlayer.class.getCanonicalName();
    private IjkMediaPlayer mIjkPlayer = null;
    private Surface mSurface = null;
	private String mDataSource = null;
	private Context mContext = null;
	private final Object mInitLock = new Object();
	
	// all possible internal states
	public static final int STATE_ERROR = -1;
	public static final int STATE_IDLE = 0;
	public static final int STATE_PREPARING = 1;
	public static final int STATE_PREPARED = 2;
	public static final int STATE_PLAYING = 3;
	public static final int STATE_PAUSED = 4;
	public static final int STATE_PLAYBACK_COMPLETED = 5;
	public static final int STATE_SUSPEND = 6;
	public static final int STATE_RESUME = 7;
	public static final int STATE_BUFFERING = 8;
	public static final int STATE_SUSPEND_UNSUPPORTED = 9;

    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int 	mCurrentState = STATE_IDLE;
    private int 	mTargetState = STATE_IDLE;
    private int 	mCurrentBufferPercentage = -1;
    private boolean mSeekFlag = false;
    
	//video size
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private int mSeekWhenPrepared;  // recording the seek position while preparing
    private int mVideoRotationDegree;
    private IjkPlayerCallback callBack = null;
    
    public KwIjkPlayer() {
        createKwVideoPlayer();
    }

    private void createKwVideoPlayer()
    {
        if(null == mIjkPlayer) {
            synchronized (mInitLock) {
                mIjkPlayer = new IjkMediaPlayer();
            }
            setEventListener();
            //提高解码画面质量
            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0 /*48*/);
            //最大缓冲时间
            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "last-high-water-mark-ms",     10000);
            //设置启动缓冲时间
            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "first-high-water-mark-ms",     2000);

            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);

            //设置log日志根目录，即打开播放器的log日志；不设置则不打开log日志
//            if (AppInfo.IS_DEBUG) {//调试模式打开日志
            IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
            //设置log日志根目录，即打开播放器的log日志；不设置则不打开log日志
            String strPath = MediaDirs.getMediaPath(MediaDirs.IJK_LOG);
            if (KwFileUtils.isExist(strPath)) {
                setAppHomeDir(strPath);
            }
//            }
            mIjkPlayer.setWakeMode(Media.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setSpeed(float speed) {
       if (mIjkPlayer!=null){
           mIjkPlayer.setSpeed(speed);
       }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public float getSpeed() {
        if (mIjkPlayer!=null){
            return mIjkPlayer.getSpeed();
        }
        return .0f;
    }

    private void setEventListener()
    {
        if( null != mIjkPlayer ) {
            mIjkPlayer.setOnPreparedListener(mPreparedListener);
            mIjkPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mIjkPlayer.setOnCompletionListener(mCompletionListener);
            mIjkPlayer.setOnErrorListener(mErrorListener);
            mIjkPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mIjkPlayer.setOnInfoListener(mInfoListener);
            mIjkPlayer.setOnSeekCompleteListener(mSeekCompleteListener);

        }
    }

    private void releaseKwIjkPlayer()
    {
//        this.stop();
    	mIjkPlayer.stop();
        this.release();
        this.mIjkPlayer = null;
    }

    public void setCallBack(IjkPlayerCallback callBack) {
        this.callBack = callBack;
    }
    
    /**
     * 设置显示SurfaceView
     * @param surfaceView
     */
    public void setSurfaceView(SurfaceView surfaceView) {
        if (surfaceView != null) {
			synchronized (mInitLock) {
            	mIjkPlayer.setDisplay(surfaceView.getHolder());
                mSurface = surfaceView.getHolder().getSurface();
			}
        }
    }

    /**
     * 设置显示Surface
     * @param surface
     */
    public void setSurface(Surface surface)
    {
        mIjkPlayer.setSurface(surface);
        mSurface = surface;
    }

    /**
     * 设置播放地址
     * @param path
     * @throws IOException
     */
    public void setDataSource(String path) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException {
        mIjkPlayer.setDataSource(path);
        mDataSource = path;
    }

    /**
     * 设置播放地址
     * @param uri
     * @throws IOException
     */
    public void setDataSource(Uri uri) throws IOException {
        setDataSource(uri.toString());
    }

    /**
     * 获取播放地址
     * @return
     */
    public String getDataSource() {
        return mIjkPlayer.getDataSource();
    }
    /**
     * 异步启动播放
     */
    public void prepareAsync() throws IllegalStateException {
        mIjkPlayer.prepareAsync();
        mCurrentState = STATE_PREPARING;
    }

    public void prepareAsync(long startPos)
    {
        mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "seek-at-start", startPos);
//        mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
        prepareAsync();
    }

    /**
     * 暂停之后再次继续播放
     */
    public void start() {
        mIjkPlayer.start();
        mCurrentState = STATE_PLAYING;
    }

    /**
     * 停止播放
     */
    public void stop() {
//        mIjkPlayer.stop();
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        releaseKwIjkPlayer();
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mCurrentState = STATE_PAUSED;
        mIjkPlayer.pause();
    }

    /**
     * 是否播放器native资源
     */
    public void release() {
        mIjkPlayer.release();
    }

    /**
     * 复位：释放资源并重新初始化；如果连续播放时不需要每次重新new一次
     */
    public void reset()
    {
        if(mIjkPlayer!= null) {
            mIjkPlayer.reset();
        }
    }

    /**
     * 获取视频宽度
     * @return
     */
    public int getVideoWidth() {
        return mIjkPlayer.getVideoWidth();
    }

    /**
     * 获取视频高度
     * @return
     */
    public int getVideoHeight() {
        return mIjkPlayer.getVideoHeight();
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying() {
        try {
            return mIjkPlayer.isPlaying();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
    };

    /**
     * seek to mses
     * @param mses
     */
    public void seekTo(long mses) {
        mIjkPlayer.seekTo(mses);
        mSeekFlag = true;
    }

    /**
     * 获取当前播放位置
     * @return
     */
    public long getCurrentPosition() {
        try {
            return mIjkPlayer.getCurrentPosition();
        }
        catch (IllegalStateException e) {
            return 0;
        }
    }

    /**
     * 获取视频总时长，直播无效
     * @return
     */
    public long getDuration() {
        try {
            return mIjkPlayer.getDuration();
        } catch (IllegalStateException e) {
            return 0;
        }
    }

    /**
     * 播放时保持屏幕常亮
     * ????设置后不生效????
     * @param screenOn
     */
    public void setScreenOnWhilePlaying(boolean screenOn) {
        mIjkPlayer.setScreenOnWhilePlaying(screenOn);
    }

    /**
     * 设置循环播放
     * @param looping
     */
    public void setLooping(boolean looping) {
        mIjkPlayer.setLooping(looping);
    }

    public boolean isLooping() {
        return mIjkPlayer.isLooping();
    }

    public void setVolume(float leftVolume, float rightVolume) {
        mIjkPlayer.setVolume(leftVolume / (float)100, rightVolume / (float)100);
    }

    public void setDisplayDisable(boolean enable)
    {
        mIjkPlayer._setDisplayDisable(enable);
    }

    /**
     * 出现异常情况时自动重连
     * @return
     * @throws IOException
     */
    public boolean restartAuto() throws IOException {
        if( (mDataSource == null) || mDataSource.isEmpty() ) {
            return false;
        }

        reset();
        this.setDataSource(mDataSource);
        this.prepareAsync();

        if(mSurface != null)
        {
	        this.setSurface(mSurface);
	        this.setScreenOnWhilePlaying(true);
        }
        return true;
    }

    /**
     * 播放下一个
     * @param path
     * @return
     * @throws IOException
     */
    public boolean playNext(String path) throws IOException {
        mDataSource = path;
        return restartAuto();
    }

    public int getPlayerState()
    {
    	return mCurrentState;
    }
    /**
     *   设置直播流(rtmp或者hls)标志；
     */
    public void setLiveStreamOpt()
    {
        mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "livestream", 1);
    }

    /**
     *   设置rtmp直播流标志；暂时没用到
     */
    public void setRtmpLiveStreamOpt()
    {
        mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "rtmp_live", 1);
    }

    /**
     * 设置app的工作根目录
     * @param path
     */
    public void setAppHomeDir(String path)
    {
        mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "app-home-dir", path);
    }

    /**
     * 设置播放器直播最大缓冲时间；默认为3秒
     * @param msec
     */
    public void setMaxCacheTime(int msec)
    {
        mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-cache-time", msec);
    }


    public void setSpectrum(boolean enable, Spectrum callback){
        IjkMediaPlayer.setSpectrum(enable, callback);
    }

    /**
     * Callback：视频大小改变
     */
    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height,
                                       int sarNum, int sarDen) {
        	Log.i(TAG, "onVideoSizeChanged: (%d" + width + "x%d)" + height);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            mVideoSarNum = sarNum;
            mVideoSarDen = sarDen;
        }
    };

    /**
     * Callback：播放器准备完成
     * Not Used Now
     */
    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
        	Log.i(TAG, "onPrepared");
            mCurrentState = STATE_PREPARED;
            mTargetState = STATE_PLAYING;

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mTargetState == STATE_PLAYING) {
//                start();
            }
        }
    };

    /**
     * Callback：播放完成
     */
    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
        	Log.i(TAG, "onCompletion");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            callBack.onPlayerStopped();
        }
    };

    /**
     * Callback：播放错误
     */
    private OnErrorListener mErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) throws IOException {
        	Log.i(TAG, "Error: %d" + framework_err +", %d!" + impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            
//			reconnect	
//            if(impl_err == IMediaPlayer.MEDIA_ERROR_EIO)
//            {
//                restartAuto();
//                return true;
//            }
            
            callBack.onEncounteredError(impl_err);
            return true;
        }
    };

    /**
     * Callback：缓冲进度(0-100)
     * 直播时总是0
     */
    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            if (mCurrentBufferPercentage != percent) {
            	Log.i(TAG, "CurrentBufferPercentage:" + percent);
                mCurrentBufferPercentage = percent;
                callBack.onBuffering(percent);
            }
        }
    };

    /**
     * Callback：?????
     */
    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) throws IOException {
        	Log.i(TAG, "onInfo: (%d" + arg1 +", %d)" + arg2);

            switch (arg1) {
                case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_TRACK_LAGGING)");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_RENDERING_START)");
                    mCurrentState = STATE_PLAYING;
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_START)");
                	if(mCurrentState == STATE_PLAYING) {
                		if(!mSeekFlag) {
                			mCurrentState = STATE_BUFFERING;
                			callBack.onStartBuffering();
                		}
                	}
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_END)");
                    if(mCurrentState == STATE_BUFFERING) {
                        if (!mSeekFlag) {
                            callBack.onEndBuffering();
                        }
                        mCurrentState = STATE_PLAYING;
                    }
                    break;
                case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2 + ")");
                    break;
                case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_BAD_INTERLEAVING)");
                    break;
                case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_NOT_SEEKABLE)");
                    break;
                case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_METADATA_UPDATE)");
                    break;
                case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_UNSUPPORTED_SUBTITLE)");
                    break;
                case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_SUBTITLE_TIMED_OUT)");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    mVideoRotationDegree = arg2;
                    Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2 + ")");
//                    setVideoRotation(arg2);
                    break;
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                	Log.i(TAG, "onInfo: (MEDIA_INFO_AUDIO_RENDERING_START):");
                    mCurrentState = STATE_PLAYING;
                    callBack.onStartPlaying();
                    break;
                case IMediaPlayer.MEDIA_INFO_AUTOSTOPPED:
                	Log.w(TAG, "onInfo: (MEDIA_INFO_AUTOSTOPPED):");
                    restartAuto();
                    break;
                default:
                    break;
            }

            return true;
        }
    };

    /**
     * Callback：Seek完成事件
     */
    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
        	Log.i(TAG, "onSeekComplete");
            callBack.onSeekComplete();
            mSeekFlag = false;
        }
    };

}
