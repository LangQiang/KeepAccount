package com.lazylite.media.remote.core.play;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.lazylite.media.Media;
import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.bean.PlayLogInfo;
import com.lazylite.media.playctrl.PlayProxy;
import com.lazylite.media.remote.AIDLPlayDelegate;
import com.lazylite.media.remote.core.down.DownCacheMgr;
import com.lazylite.media.remote.core.down.DownloadDelegate;
import com.lazylite.media.remote.core.down.DownloadMgr;
import com.lazylite.media.remote.core.down.DownloadProxy;
import com.lazylite.media.remote.core.ijkwrapper.IPlayCtrl;
import com.lazylite.media.remote.core.ijkwrapper.IjkPlayerPlayCtrl;
import com.lazylite.media.remote.core.ijkwrapper.PlayDelegate;
import com.lazylite.media.remote.core.ijkwrapper.RemoteProcNotify;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.mod.permission.Permission;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.receiver.sdcard.SDCardUtils;
import com.lazylite.mod.utils.KwFileUtils;


//by haiping
public final class PlayManager {
	private static final String TAG	= "PlayManager";
	public static final String AUDIO_FOCUS_RECEIVER = "cn.kuwo.service.audio.focus";
	public static final int AUDIO_FOCUS_CHANGED = 1;
	public static final int CALL_STATE_CHANGED = 2;

	private static final int PAUSE_TYPE_DEFAULT = 0;
	private static final int PAUSE_TYPE_FOCUS = 1;
	private static final int PAUSE_TYPE_PHONE = 2;
	private int pauseByType = PAUSE_TYPE_DEFAULT;
	public AudioInfo audioInfo;

	public static PlayManager getInstance() {
		return instance;
	}

	private PlayManager() {
	}

	public void init(ThreadMessageHandler handler) {
		threadHandler = handler;
		MessageManager.getInstance().syncRunTargetHandler(threadHandler.getHandler(),
				new MessageManager.Runner() {
					@Override
					public void call() {
						requestAudioFocus();
						attachEvent();
					}
				});
	}

	private void attachEvent() {
		audioMgr = (AudioManager) Media.getContext().getSystemService(Context.AUDIO_SERVICE);
		try {
			maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 来电动作监听
		TelephonyManager tmgr = (TelephonyManager) Media.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		try { // 无语的安卓，这是要闹哪样
			if (kwPhoneStateListener == null) {
				kwPhoneStateListener = new KwPhoneStateListener();
			}
			//弱引用回收掉了 此处强持有下 不然有些版本回调无效
			tmgr.listen(kwPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		} catch (Exception e) {
		}
	}

	public void setDelegate(AIDLPlayDelegate delegate) {
		this.delegate = delegate;
		if (ijkPlayCtrl != null) {
			ijkPlayCtrl.setDelegate(delegate);
		}
	}

	private String getExistFilePath(AudioInfo audioInfo) {
		if (!TextUtils.isEmpty(audioInfo.mFilePath) && KwFileUtils.isExist(audioInfo.mFilePath)) {
			return audioInfo.mFilePath;
		}
		String path = DownCacheMgr.getFinishedTingshu(audioInfo.albumId, audioInfo.rid, audioInfo.musicMd5);
		if (!TextUtils.isEmpty(path)) {
			return path;
		}
		return null;
	}



	public void cancelPrefetch() {
		if (prefetchID>-1) {
			DownloadMgr.removeTask(prefetchID);
			prefetchID = -1;
		}
		prefetchRid = -1;
	}

	public void stop() {
		if (playCtrl() != null) playCtrl().stop();
	}
	
	public void cancel() {
		if (playCtrl() != null) playCtrl().stop();
	}
	
	public void resume() {
		if (playCtrl() != null && playCtrl().getStatus() == PlayProxy.Status.PAUSE) {
			requestAudioFocus();
			playCtrl().resume();
		}
	}

	public PlayProxy.Status getStatus() {
		if (playCtrl() == null) {
			return PlayProxy.Status.INIT;
		}
		return playCtrl().getStatus();
	}

	public void pause() {
		if (playCtrl() == null) {
			return;
		}
		if (playCtrl().getStatus() == PlayProxy.Status.PLAYING || playCtrl().getStatus() == PlayProxy.Status.BUFFERING) {
			playCtrl().pause();
		}
	}

	public int getDuration() {
		if (playCtrl() == null) {
			return 0;
		}
		return playCtrl().getDuration();
	}

	public int getCurrentPos() {
		if (playCtrl() == null) {
			return 0;
		}
		return playCtrl().getCurrentPos();
	}

	public int getBufferPos() {
		if (playCtrl() == null) {
			return 0;
		}
		return playCtrl().getBufferPos();
	}

	public int getPreparingPercent() {
		if (playCtrl() == null) {
			return 0;
		}
		return playCtrl().getPreparingPercent();
	}

	public void seek(int pos) {
		if (playCtrl() != null) playCtrl().seek(pos);
	}

	public int getMaxVolume() {
		return maxVolume;
	}

	public int getVolume() {
		return audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void setVolume(final int vol) {
		if (vol < 0 || vol > maxVolume) {
			return;
		}
		if (vol == getVolume()) {
			return;
		}

		boolean isCurrentMute = isMute();
		volumeBeforeMute = vol;
		if (vol > 0 == isCurrentMute) {
			setMute(!isCurrentMute);
		}
		audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
		
		MessageManager.getInstance().asyncRun(
				new MessageManager.Runner() {
					public void call() {
						if (delegate != null)
							try {
								delegate.PlayDelegate_SetVolume(vol);
							} catch (Throwable e) {
								Log.e(TAG, e.getMessage() + "");
							}
					}
				});
	}

	public boolean isMute() {
		return audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC) == 0;
	}

	public void setMute(boolean bMute) {
		final boolean isNowMute=isMute();
		if (bMute != isNowMute) {
			if (bMute) {
				volumeBeforeMute = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
				audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			} else {
				audioMgr.setStreamMute(AudioManager.STREAM_MUSIC, false);
				audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, volumeBeforeMute, 0);
			}
			MessageManager.getInstance().asyncRun(
					new MessageManager.Runner() {
						public void call() {
							if (delegate != null) {
								try {
									delegate.PlayDelegate_SetMute(isNowMute);
								} catch (Throwable e) {
									Log.e(TAG, e.getMessage() + "");
								}
							}
						}
					});
		}
	}
	
	public void updateVolume() {
		MessageManager.getInstance().asyncRun(
				new MessageManager.Runner() {
					public void call() {
						if (delegate != null) {
							try {
								delegate.PlayDelegate_SetMute(isMute());
								delegate.PlayDelegate_SetVolume(getVolume());
							} catch (Throwable e) {
								Log.e(TAG, e.getMessage() + "");
							}
						}
					}
				});
	}
	
	public int getPlayContentType() {
		return curPlayContent;
	}


	public PlayLogInfo getPlayLogInfo() {
		PlayLogInfo info = new PlayLogInfo();
		playCtrl().getPlayLogInfo(info);
		return info;
	}

	private void requestAudioFocus() {
		/*
		* 只要有申请焦点动作 重置type 忽略音频焦点的播放通知
		* 防止有些应用申请焦点不abandon 之后用户切回酷我主动操作播放暂停 一旦释放焦点会让用户认为酷我自动播放
		* */
		setPauseType(PAUSE_TYPE_DEFAULT);
		try {
			if (onAudioFocusChangeListener == null) {
				onAudioFocusChangeListener = new KwOnAudioFocusChangeListener();
			}
		} catch (Throwable e) {
			return;
		}
		try {
			if (audioMgr.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				audioMgr.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
						AudioManager.AUDIOFOCUS_GAIN);
			}
		} catch (Throwable e) {
		}
		try {
			if (audioMgr.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_ALARM,
					AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				audioMgr.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_ALARM,
						AudioManager.AUDIOFOCUS_GAIN);
			}
		} catch (Throwable e) {
		}
	}

	private void abandonAudioFocus() {
		if (onAudioFocusChangeListener != null) {
			audioMgr.abandonAudioFocus(onAudioFocusChangeListener);
			onAudioFocusChangeListener = null;
		}
	}


	


	private void useIjkPlayCtrl(boolean fftEnable) {
		if (ijkPlayCtrl == null) {
			ijkPlayCtrl = new IjkPlayerPlayCtrl();
		}

		//设置频谱开关（无损开启）
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			//4.4以下不支持
			ijkPlayCtrl.setFFTDataEnableReceive(false);
		} else {
			ijkPlayCtrl.setFFTDataEnableReceive(fftEnable);
		}


		ijkPlayCtrl.setMessageHandler(threadHandler);
		ijkPlayCtrl.setDelegate(delegate);
//		ijkPlayCtrl.setPlayInfo(m);
        
		curentPlayCtrl = ijkPlayCtrl;
	}
	
	private IPlayCtrl playCtrl() {
		return curentPlayCtrl;
	}







	private void notifyPreStart(final boolean isLocal) {
		if (delegate != null) {
			MessageManager.getInstance().asyncRun(
					new MessageManager.Runner() {
						public void call() {
							try {
								delegate.PlayDelegate_PreStart(!isLocal);
							} catch (Throwable ignore) {
							}
						}
					});
            RemoteProcNotify.notifyPrePlay(!isLocal);
		}
	}

	protected void notifyError(final PlayDelegate.ErrorCode error) {
		MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				if (delegate != null) {
					try {
						delegate.PlayDelegate_Failed(error.ordinal());
					} catch (Throwable e) {
					}
				}
			}
		});
	}
	private int                             curPlayContent = PlayDelegate.PlayContent.MUSIC.ordinal();
	private AIDLPlayDelegate				delegate;

	private IjkPlayerPlayCtrl               ijkPlayCtrl;
	private IPlayCtrl						curentPlayCtrl;
	private AudioManager audioMgr;
	private int								maxVolume;
	private int								volumeBeforeMute;
	private static PlayManager				instance	= new PlayManager();
	private ThreadMessageHandler			threadHandler;
	private KwOnAudioFocusChangeListener	onAudioFocusChangeListener;
	private KwPhoneStateListener 			kwPhoneStateListener = null;

	public void play(AudioInfo audioInfo, int continuePos) {
		//播放听书
        this.audioInfo = audioInfo;
        RemoteProcNotify.notifyPlay();
		curPlayContent = PlayDelegate.PlayContent.TINGSHU.ordinal();
		if (playCtrl() != null) {
			playCtrl().stop();
		}
		requestAudioFocus();

		if (!SDCardUtils.isAvailable()) {
			notifyError(PlayDelegate.ErrorCode.NO_SDCARD);
			return;
		}
		if (audioInfo != null) {
			useIjkPlayCtrl(false);
			playCtrl().setEndTime(-1);
			PlayDelegate.ErrorCode retCode;
			String path = getExistFilePath(audioInfo);
			if (TextUtils.isEmpty(path) /*|| !Permission.checkSelfPermission(Media.getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})*/) {
				notifyPreStart(false);
				retCode = playCtrl().playNet(audioInfo, continuePos);
			} else {
				audioInfo.mFilePath = path;
				notifyPreStart(true);
				retCode = playCtrl().playLocal(audioInfo, continuePos);
			}
			if (retCode != PlayDelegate.ErrorCode.SUCCESS) {
				notifyError(retCode);
			}
			if (tingshuPrefetchRid != -1 && audioInfo.rid != tingshuPrefetchRid) {
				cancleTingshuPrefetch();
				deleteTingshuPrefetchFile();
			}
		} else {
			notifyError(PlayDelegate.ErrorCode.DECODE_FAILE);
		}
	}

	public void prefetch(AudioInfo iAudio) {
		if (tingshuPrefetchID > -1) {
			//说明有预取的任务在运行,要把上一个预取的任务移除掉
			cancleTingshuPrefetch();
		}
		if (iAudio == null) {
			return;
		}
		String filePath = getExistFilePath(iAudio);
		if (!TextUtils.isEmpty(filePath)) {
			return;
		}
		if (NetworkStateUtil.isOnlyWifiConnect()) {
			return;
		}
		if (iAudio.albumId <= 0 || iAudio.rid <= 0) {
			return;
		}
		tingshuPrefetchRid = iAudio.rid;
		tingshuPrefetchID = DownloadMgr.getInstance(DownloadProxy.DownGroup.TINGSHU).addTask(iAudio, DownloadProxy.DownType.TSPREFETCH
				,tingshuPrefetchDelegate, threadHandler.getHandler());
	}

    public void clearCache(AudioInfo audioInfo) {
        String path = DownCacheMgr.getFinishedTingshu(audioInfo.albumId, audioInfo.rid, audioInfo.musicMd5);
        DownCacheMgr.deleteCacheFile(path);
        AntiStealing.removeOneCache(audioInfo);
    }

	public void clearPlayList() {
		RemoteProcNotify.notifyClearPlayList();
	}

	@SuppressLint("NewApi")
	private class KwOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
		@Override

		public void onAudioFocusChange(final int focusChange) {

			switch (focusChange) {
				case AudioManager.AUDIOFOCUS_LOSS:// 你已经丢失了音频焦点比较长的时间了．你必须停止所有的音频播放．因为预料到你可能很长时间也不能再获音频焦点，所以这里是清理你的资源的好地方．比如，你必须释放
				case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:// 你临时性的丢掉了音频焦点，但是你被允许继续以低音量播放，而不是完全停止．
					// 音频焦点丢失不做处理，继续播放
					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:// 你临时性的丢掉了音频焦点，很快就会重新获得．你必须停止所有的音频播放，但是可以保留你的资源，因为你可能很快就能重新获得焦点．
					// 双卡手机的副卡接听或者拨打电话会走到这一步（不会被电话状态监听器监听到 MyPhoneStateListener）
					onLostAudioFocus(PAUSE_TYPE_FOCUS);
					break;
				case AudioManager.AUDIOFOCUS_GAIN:
				case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
				case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE:
				case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
					onGainAudioFocus(PAUSE_TYPE_FOCUS);
					break;
			}
		}
	}

	private void sendAudioFocusChangedBroadCast(int audioFocusChanged, int state) {
		Intent intent = new Intent(AUDIO_FOCUS_RECEIVER);
		intent.putExtra("action", audioFocusChanged);
		intent.putExtra("state", state);
		Media.getContext().sendBroadcast(intent);
	}

	private class KwPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(final int state, final String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			sendAudioFocusChangedBroadCast(CALL_STATE_CHANGED, state);
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					onGainAudioFocus(PAUSE_TYPE_PHONE);
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
				case TelephonyManager.CALL_STATE_RINGING:
					onLostAudioFocus(PAUSE_TYPE_PHONE);
					break;
				default:
					break;
			}
		}
	};

	private void onLostAudioFocus(int type) {
		if (playCtrl() == null) {
			return;
		}
		boolean isPlayOrBuffering = playCtrl().getStatus() == PlayProxy.Status.PLAYING || playCtrl().getStatus() == PlayProxy.Status.BUFFERING;
		//正在播放才暂停 并给pauseByType赋值
		if (isPlayOrBuffering) {
			if (pauseByType == PAUSE_TYPE_DEFAULT) { //如果能走进来 正常情况都会是0 加一步保护
				setPauseType(type);
			}
			MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
				@Override
				public void call() {
					PlayManager.getInstance().pause();
				}
			});
		}
	}

	private void onGainAudioFocus(int type) {
		if (playCtrl() == null) {
			return;
		}
		if (type == pauseByType) {
			setPauseType(PAUSE_TYPE_DEFAULT);
			MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
				@Override
				public void call() {
					PlayManager.getInstance().resume();
				}
			});
		}
	}

	private void setPauseType(int type) {
		pauseByType = type;
	}

	
	public void setNoRecoverPause() {
		setPauseType(PAUSE_TYPE_DEFAULT);
	}
	
	private int prefetchID = -1;
	private long prefetchRid = -1;
	private String prefetchFile;
	private DownloadDelegate prefetchDelegate = new DownloadDelegate() {
		@Override
		public void DownloadDelegate_Start(int id, String url, String tempPath,
                                           int totalLen, final int currentLen, int bitrate,
                                           final DownloadDelegate.DataSrc dataSrc) {
			if (DownCacheMgr.isCacheSong(tempPath)) {
				prefetchFile=tempPath;
			}
		}
		
		@Override
		public void DownloadDelegate_Progress(int id, int totalLen, int current, float speed) {
		}
		
		@Override
		public void DownloadDelegate_Finish(int id, DownloadDelegate.ErrorCode err, String savePath) {
			if (err==DownloadDelegate.ErrorCode.SUCCESS && DownCacheMgr.isCacheSong(savePath)) {
				prefetchFile = savePath;
			}
			prefetchID = -1;
		}
	};


	public void setSpectrumEnable (boolean enable){
		if(ijkPlayCtrl != null){
			ijkPlayCtrl.setSpectrumEnable(enable);
		}
	}

	private int tingshuPrefetchID = -1;
	private long tingshuPrefetchRid = -1;
	private String tingshuPrefetchFile;
	private DownloadDelegate tingshuPrefetchDelegate = new DownloadDelegate() {
		@Override
		public void DownloadDelegate_Start(int id, String url, String tempPath,
                                           int totalLen, final int currentLen, int bitrate,
                                           final DownloadDelegate.DataSrc dataSrc) {
			if (!TextUtils.isEmpty(tempPath)) {
				tingshuPrefetchFile = tempPath;
			}
		}

		@Override
		public void DownloadDelegate_Progress(int id, int totalLen, int current, float speed) {
		}

		@Override
		public void DownloadDelegate_Finish(int id, DownloadDelegate.ErrorCode err, String savePath) {
			if (err==DownloadDelegate.ErrorCode.SUCCESS && !TextUtils.isEmpty(savePath)) {
				tingshuPrefetchFile = savePath;
				if (delegate != null) {
					MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
						@Override
						public void call() {
							if (delegate != null ) {
								try {
									delegate.PlayDelegate_DownloadFinished(tingshuPrefetchFile, tingshuPrefetchRid);
								} catch (Throwable e) {
									Log.e(TAG, e.getMessage() + "");
								}
							}
						}
					});
				}
			}
			tingshuPrefetchID = -1;

		}
	};

	private void deleteTingshuPrefetchFile() {
		if (KwFileUtils.isExist(tingshuPrefetchFile)) {
			DownCacheMgr.deleteCacheFile(tingshuPrefetchFile);
		}
		tingshuPrefetchRid = -1;
		tingshuPrefetchID = -1;
		tingshuPrefetchFile = null;
	}

	public void cancleTingshuPrefetch() {
		if (tingshuPrefetchID > -1) {
			DownloadMgr.removeTask(tingshuPrefetchID);
			tingshuPrefetchID = -1;
		}
		tingshuPrefetchRid = - 1;
	}


	public void setSpeed(float speed) {
		if (playCtrl() != null) {
			playCtrl().setSpeed(speed);
		}
	}

	public float getSpeed() {
		if (playCtrl() != null) {
			return playCtrl().getSpeed();
		}
		return .0f;
	}
}
