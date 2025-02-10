package com.lazylite.media.remote.core.ijkwrapper;

import android.util.Log;

import com.lazylite.media.playctrl.PlayProxy;
import com.lazylite.media.remote.AIDLPlayDelegate;
import com.lazylite.mod.messagemgr.MessageManager;

//import cn.kuwo.service.remote.kwplayer.KwLog;

public class PlayStateNotify {
    
	private static final String TAG  =  "PlayStateNotify";
	private static int					nextNotifyVersion	= 100;
	private static int				    currentNotifyVersion = 0;
	private AIDLPlayDelegate			delegate = null;
	
	
    public PlayStateNotify(){
        Log.i(TAG, "new PlayStateNotify");
    }

    public void incVersion()
    {
        this.currentNotifyVersion = this.nextNotifyVersion++;
        Log.i(TAG, "currentNotifyVersion=" + this.currentNotifyVersion);
    }
    
    public void setDelegate(AIDLPlayDelegate delegate) {
        Log.i(TAG, "set delegate:" + delegate);
        this.delegate = delegate;
    }
	
	protected void notifyStart(final PlayProxy.Status status, final long realStartTime) {
        
        Log.i(TAG, "notifyStart status=" + status + " realStartTime=" + realStartTime);
         
		final long realTime = realStartTime == 0 ? System.currentTimeMillis() : realStartTime;

		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion) {
			@Override
			public void call() {
				//PlayHandler.getInstance().PlayDelegate_RealStart(realTime);
				if (delegate != null && callVersion == currentNotifyVersion && status != PlayProxy.Status.STOP) {
					try {
						delegate.PlayDelegate_RealStart(realTime);
                        RemoteProcNotify.notifyRealPlay();
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
						//if(MiLockUtils.isUseXiaoMiLockScreen()){
						//	MiLockUtils.stopMiLockService();
						//}
						//PlayHandler.getInstance().prefetchMusic();
					}
				}
                else{
                    Log.e(TAG, "notifyStart fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
                }
			}
		});
	}
	
	protected void notifyPause(final PlayProxy.Status status) {
        Log.i(TAG, "notifyPause status=" + status);
        
		if (delegate == null) {
            Log.i(TAG, "notifyPause fail delegate==null");
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion) {
			@Override
			public void call() {
				//PlayHandler.getInstance().PlayDelegate_Pause();
				
				if (delegate != null && callVersion == currentNotifyVersion && status != PlayProxy.Status.STOP) {
					try {
						delegate.PlayDelegate_Pause();
                        RemoteProcNotify.notifyPause();
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
                else{
                    Log.e(TAG, "notifyPause fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
                }
			}
		});
	}

	protected void notifyResume(final PlayProxy.Status status) {

        Log.i(TAG, "notifyResume status=" + status);
        
		if (delegate == null) {
            Log.e(TAG, "notifyResume fail delegate==null");
			return;
		}
		
		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion) {
			@Override
			public void call() {
				//PlayHandler.getInstance().PlayDelegate_Continue();
				if (delegate != null && callVersion == currentNotifyVersion && status != PlayProxy.Status.STOP) {
					try {
						delegate.PlayDelegate_Continue();
                        RemoteProcNotify.notifyContinue();
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
                else{
                    Log.e(TAG, "notifyResume fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
                }
			}
		});
	}
	
	/**
	 * 当前播放的是Music还是KSingPro，还是CD，默认播放的Music
	 * @return
	 */
//	protected int getPlayType(boolean isPlayCD, boolean isPlaySing){
//		if (isPlaySing) {
//			return PlayContent.KSING.ordinal();
//		} else {
//			if (isPlayCD) {
//				return PlayContent.CD.ordinal();
//			} else {
//				return PlayContent.MUSIC.ordinal();
//			}
//		}
//
//	}

	// stop鍦╟ancle涔嬪悗杩樺彲鑳介渶瑕侀€氱煡鍑哄幓锛屾墍浠ヤ笉绠otifyVersion鐗堟湰
	protected void notifyStop(final String path, final boolean end, final PlayDelegate.PlayContent playContent) {
	    Log.i(TAG, "notifyStop end=" + end + "path=" + path);
        
		KwWifiLock.unLock();
		if (delegate == null) {
            Log.e(TAG, "notifyStop fail delegate==null");
			return;
		}
        
		MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				//PlayHandler.getInstance().PlayDelegate_Stop(end, path);
				if (delegate != null) {
					try {
						delegate.PlayDelegate_Stop(end, path, playContent.ordinal());
                        RemoteProcNotify.notifyStop(end);
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
			}
		});
	}

	protected void notifySeekSuccess(final int pos, final PlayDelegate.PlayContent playContent) {
		KwWifiLock.unLock();
		if (delegate == null) {
			Log.e(TAG, "notifyStop fail delegate==null");
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				//PlayHandler.getInstance().PlayDelegate_Stop(end, path);
				if (delegate != null) {
					try {
						delegate.PlayDelegate_SeekSuccess(pos, playContent.ordinal());
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
			}
		});
	}

	protected void notifyError(final PlayDelegate.ErrorCode error, String desc) {
        Log.i(TAG, "notifyError error=" + error);

		KwWifiLock.unLock();
		if (delegate == null) {
            Log.e(TAG, "notifyError fail delegate==null");
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion) {
			@Override
			public void call() {
				//PlayHandler.getInstance().PlayDelegate_Failed(error);
				if (delegate != null && callVersion == currentNotifyVersion) {
					Log.e(TAG, "notifyError success callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
					try {
						delegate.PlayDelegate_Failed(error.ordinal());
                        RemoteProcNotify.notifyFailed(error.name());
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
                else{
                    Log.e(TAG, "notifyError fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
                }
			}
		});
	}

	protected void notifyBuffering(final PlayProxy.Status status) {
        
        Log.i(TAG, "notifyBuffering status=" + status);
        
		if (delegate == null) {
            Log.e(TAG, "notifyBuffering fail delegate==null");
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion) {
			@Override
			public void call() {
				//PlayHandler.getInstance().PlayDelegate_WaitForBuffering();
				if (delegate != null && callVersion == currentNotifyVersion && status != PlayProxy.Status.STOP) {
					try {
						delegate.PlayDelegate_WaitForBuffering();
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				} else{
                    Log.e(TAG, "notifyBuffering fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
                }
			}
		});
	}

	protected void notifyBufferingFinish() {

        Log.i(TAG, "notifyBufferingFinish");

		if (delegate == null) {
            Log.e(TAG, "notifyBufferingFinish fail delegate==null");
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion) {
			@Override
			public void call() {
				//PlayHandler.getInstance().PlayDelegate_WaitForBufferingFinish();
				if (delegate != null && callVersion == currentNotifyVersion) {
					try {
						delegate.PlayDelegate_WaitForBufferingFinish();
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
                else{
                    Log.e(TAG, "notifyBufferingFinish fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
                }
			}
		});
	}

	protected void notifyPlayProgress(final int total, final int playPos, final int bufferPos)
	{
		Log.i(TAG, "notifyPlayProgress");

		if (delegate == null) {
			Log.e(TAG, "notifyPlayProgress fail delegate==null");
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion) {
			@Override
			public void call() {
				if (delegate != null && callVersion == currentNotifyVersion) {
					try {
						delegate.PlayDelegate_PlayProgress(total, playPos, bufferPos);
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage()+"");
					}
				}
				else{
					Log.e(TAG, "notifyPlayProgress fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
				}
			}
		});
	}

	protected void notifyDownloadFinished(final PlayProxy.Status status, final String savePath, final long id) {
		Log.i(TAG, "notifyDownloadFinished");
		if (delegate != null) {
			MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion) {
				@Override
				public void call() {
					if (delegate != null && status != PlayProxy.Status.STOP
							&& callVersion == currentNotifyVersion) {
						try {
							delegate.PlayDelegate_DownloadFinished(savePath, id);
						} catch (Throwable e) {
							Log.e(TAG, e.getMessage() + "");
						}
					}
				}
			});
		}
    }


	public void notifyFFTDataReceive(final float[] left, final float[] right) {
		if (delegate == null) {
			Log.e(TAG, "notifyFFTDataReceive fail delegate==null");
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion){

			@Override
			public void call() {
				if (delegate != null && callVersion == currentNotifyVersion) {
					try {
						delegate.PlayDelegate_onFFTDataReceive(left,right);
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
				else{
					Log.e(TAG, "notifyFFTDataReceive fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
				}
			}
		});
	}

	public void notifyCacheProgress(int current, int totalLen) {
		if (delegate == null) {
			Log.e(TAG, "notifyCacheProgress fail delegate==null");
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner(currentNotifyVersion){

			@Override
			public void call() {
				if (delegate != null && callVersion == currentNotifyVersion) {
					try {
						delegate.PlayDelegate_CacheProgress(current, totalLen);
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
				else{
					Log.e(TAG, "notifyCacheProgress fail callVersion=" + callVersion + "currentNotifyVersion" + currentNotifyVersion);
				}
			}
		});
	}
}
