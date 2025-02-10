package com.lazylite.media.remote;

import android.os.RemoteException;

import com.lazylite.media.remote.core.ijkwrapper.PlayDelegate;
import com.lazylite.mod.messagemgr.MessageManager;

// by haiping
public class AIDLPlayDelegateImpl extends AIDLPlayDelegate.Stub{
	
	private static PlayDelegate delegate;
	public static void setDelegate(PlayDelegate d) {
		delegate = d;
	}

	@Override
	public void PlayDelegate_PreStart(final boolean buffering) {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_PreStart(buffering);
			}
		});
	}

	@Override
	public void PlayDelegate_RealStart(final long realTime) {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_RealStart(realTime);
			}
		});
	}

	@Override
	public void PlayDelegate_Pause() {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_Pause();
			}
		});
	}

	@Override
	public void PlayDelegate_Continue() {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_Continue();
			}
		});
	}

	@Override
	public void PlayDelegate_Failed(final int error) {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_Failed(PlayDelegate.ErrorCode.values()[error]);
			}
		});
	}

	@Override
	public void PlayDelegate_Stop(final boolean end, final String savePath, final int playType) {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_Stop(end,savePath,playType);
			}
		});
	}

	@Override
	public void PlayDelegate_SeekSuccess(final int pos, final int playType) throws RemoteException {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_SeekSuccess(pos, playType);
			}
		});
	}

	@Override
	public void PlayDelegate_PlayProgress(final int total, final int playPos, final int bufferPos) {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_PlayProgress(total,playPos,bufferPos);
			}
		});
	}

	@Override
	public void PlayDelegate_WaitForBuffering() {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_WaitForBuffering();
			}
		});
	}

	@Override
	public void PlayDelegate_WaitForBufferingFinish() {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_WaitForBufferingFinish();
			}
		});
	}

	@Override
	public void PlayDelegate_CacheProgress(int currentProgress, int total) throws RemoteException {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_CacheProgress(currentProgress, total);
			}
		});
	}

	@Override
	public void PlayDelegate_DownloadFinished(final String savePath, final long id) {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_DownloadFinished(savePath, id);
			}
		});
	}

	@Override
	public void PlayDelegate_SetVolume(final int vol) {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_SetVolume(vol);
			}
		});
	}

	@Override
	public void PlayDelegate_SetMute(final boolean mute) {
		if (delegate == null) {
			return;
		}
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				delegate.PlayDelegate_SetMute(mute);
			}
		});
	}

	@Override
	public void PlayDelegate_OnRestart() throws RemoteException {
		MessageManager.getInstance().asyncRun(200, new MessageManager.Runner() {
			@Override
			public void call() {
				if (delegate == null) {
					return;
				}
				delegate.PlayDelegate_OnRestart();
			}
		});
	}

	@Override
	public void PlayDelegate_onFFTDataReceive(final float[] leftFFT, final float[] rightFFT) throws RemoteException {
		MessageManager.getInstance().syncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				if(delegate==null){
					return;
				}
				delegate.PlayDelegate_onFFTDataReceive(leftFFT,rightFFT);
			}
		});
	}

}
