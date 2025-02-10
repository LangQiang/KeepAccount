package com.lazylite.media.remote;

import android.os.RemoteException;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.bean.PlayLogInfo;
import com.lazylite.media.remote.core.ijkwrapper.PlayStateNotify;
import com.lazylite.media.remote.core.ijkwrapper.RemoteProcNotify;
import com.lazylite.media.remote.core.play.PlayManager;
import com.lazylite.media.utils.MediaDirs;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.utils.KwFileUtils;

//by haiping
public final class AIDLRemoteInterfaceImpl extends AIDLRemoteInterface.Stub {

	private static ThreadMessageHandler playThreadHandler;

	public AIDLRemoteInterfaceImpl(ThreadMessageHandler play) {
		playThreadHandler = play;
	}
	
	@Override
	public void onConnect() throws RemoteException {

	}
	
	@Override
	public void killYourself() throws RemoteException {
		stop();

		MessageManager.getInstance().asyncRun(200, new MessageManager.Runner() {
			@Override
			public void call() {
				NetworkStateUtil.release();
				KwFileUtils.deleteFile(MediaDirs.getMediaPath(MediaDirs.TEMP)); // 每次退出清空temp
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		});
	}


	@Override
	public void setDelegate(final AIDLPlayDelegate delegate) throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().setDelegate(delegate);
			}
		});
	}


	@Override
	public void cancelPrefetch() throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().cancleTingshuPrefetch();
			}
		});
	}

	@Override
	public void play(final AudioInfo iAudio, final int continuePos) throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().play(iAudio, continuePos);
			}
		});
	}

	@Override
	public void prefetch(final AudioInfo iAudio) throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().prefetch(iAudio);
			}
		});
	}

	@Override
	public void stop() throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().stop();
			}
		});
	}
	@Override
	public void cancel() throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().cancel();
			}
		});
	}

	@Override
	public void clearPlayList() throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().clearPlayList();
			}
		});
	}

	@Override
	public void pause() throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().pause();
			}
		});
	}

	@Override
	public void resume() throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().resume();
			}
		});
	}

	@Override
	public void seek(final int pos) throws RemoteException {
		MessageManager.getInstance().asyncRunTargetHandler(playThreadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				PlayManager.getInstance().seek(pos);
			}
		});
	}

	@Override
	public int getStatus() throws RemoteException {
		return PlayManager.getInstance().getStatus().ordinal();
	}

	@Override
	public int getDuration() throws RemoteException {
		return PlayManager.getInstance().getDuration();
	}

	@Override
	public int getCurrentPos() throws RemoteException {
		return PlayManager.getInstance().getCurrentPos();
	}

	@Override
	public int getBufferPos() throws RemoteException {
		return PlayManager.getInstance().getBufferPos();
	}

	@Override
	public int getPreparingPercent() throws RemoteException {
		return PlayManager.getInstance().getPreparingPercent();
	}

	@Override
	public int getMaxVolume() throws RemoteException {
		return PlayManager.getInstance().getMaxVolume();
	}

	@Override
	public int getVolume() throws RemoteException {
		return PlayManager.getInstance().getVolume();
	}

	@Override
	public void setVolume(int volume) throws RemoteException {
		PlayManager.getInstance().setVolume(volume);
	}

	@Override
	public boolean isMute() throws RemoteException {
		return PlayManager.getInstance().isMute();
	}

	@Override
	public void setMute(boolean mute) throws RemoteException {
		PlayManager.getInstance().setMute(mute);
	}

	@Override
	public void setNoRecoverPause() throws RemoteException {
		PlayManager.getInstance().setNoRecoverPause();
	}

	@Override
	public void updateVolume() throws RemoteException {
		PlayManager.getInstance().updateVolume();
	}

	@Override
	public PlayLogInfo getPlayLogInfo() throws RemoteException {
		return PlayManager.getInstance().getPlayLogInfo();
	}

	@Override
	public void setSpectrumEnable(boolean enable) throws RemoteException {
		PlayManager.getInstance().setSpectrumEnable(enable);
	}

	@Override
	public void setSpeed(float speed){
		PlayManager.getInstance().setSpeed(speed);
	}

	@Override
	public float getSpeed(){
		return PlayManager.getInstance().getSpeed();
	}

    @Override
    public void clearCache(AudioInfo audioInfo) throws RemoteException {
        PlayManager.getInstance().clearCache(audioInfo);
    }
}
