package com.lazylite.media.playctrl;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.bean.PlayLogInfo;
import com.lazylite.media.remote.AIDLPlayDelegateImpl;
import com.lazylite.media.remote.AIDLRemoteInterface;
import com.lazylite.media.remote.core.ijkwrapper.PlayDelegate;
import com.lazylite.media.remote.service.RemoteConnection;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;

public final class PlayProxy {

	private long lastMsgTime;

	private final ThreadMessageHandler threadHandler;

    public void clearCache(AudioInfo audioInfo) {
        asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                try {
                    RemoteConnection.getInstance().getInterface().clearCache(audioInfo);
                } catch (Throwable e) {
                    connectService();
                }
            }
        });
    }

    public enum ErrorCode {
		SUCCESS,
		TOOFAST
	}

	public enum Status {
		INIT, // 启动没播过歌
		PLAYING,
		BUFFERING, // 播放中，等待缓冲
		PAUSE,
		STOP
	}

	public void setDelegate(final PlayDelegate delegate) {
		AIDLPlayDelegateImpl.setDelegate(delegate);
	}


	public ErrorCode play(final AudioInfo audioInfo, final int continuePos) {
		try {
			RemoteConnection.getInstance().getInterface().play(audioInfo, continuePos);
		} catch (Throwable e) {
			if (!RemoteConnection.getInstance().isConnected()) {
				RemoteConnection.getInstance().connect(() -> {
					try {
						RemoteConnection.getInstance().getInterface().play(audioInfo, continuePos);
					} catch (Throwable e1) {
						RemoteConnection.getInstance().disconnect();
					}
				});
			}
		}

		return ErrorCode.SUCCESS;
	}


	public void prefetch(final AudioInfo audioInfo) {
		asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				try {
					RemoteConnection.getInstance().getInterface().prefetch(audioInfo);
				} catch (Throwable e) {
					connectService();
				}
			}
		});
	}

	public void cancelPrefetch() {
		asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				try {
					RemoteConnection.getInstance().getInterface().cancelPrefetch();
				} catch (Throwable e) {
					connectService();
				}
			}
		});
	}


	public boolean stop() {
		return asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				try {
					RemoteConnection.getInstance().getInterface().stop();
				} catch (Throwable e) {
					connectService();
				}
			}
		});
	}
	
	public boolean cancel() {
		return asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				try {
					RemoteConnection.getInstance().getInterface().cancel();
				} catch (Throwable e) {
					connectService();
				}
			}
		});
	}

	public boolean pause() {
		return asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				try {
					RemoteConnection.getInstance().getInterface().pause();
				} catch (Throwable e) {
					connectService();
				}
			}
		});
	}

	public boolean resume() {
		return asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				try {
					RemoteConnection.getInstance().getInterface().resume();
				} catch (Throwable e) {
					connectService();
				}
			}
		});
	}

	public boolean seek(final int pos) {
		return asyncRun(false, new MessageManager.Runner() {
			@Override
			public void call() {
				try {
					RemoteConnection.getInstance().getInterface().seek(pos);
				} catch (Throwable e) {
					connectService();
				}
			}
		});
	}

	public Status getStatus() {
		AIDLRemoteInterface remoteInterface = RemoteConnection.getInstance().getInterface();
		if (remoteInterface != null) {
			try {
				return Status.values()[remoteInterface.getStatus()];
			} catch (Throwable e) {
				connectService();
			}
		}
		return Status.INIT;
	}

	public int getDuration() {
		try {
			return RemoteConnection.getInstance().getInterface().getDuration();
		} catch (Throwable e) {
			connectService();
			return 0;
		}
	}

	public int getCurrentPos() {
		try {
			return RemoteConnection.getInstance().getInterface().getCurrentPos();
		} catch (Throwable e) {
			connectService();
			return 0;
		}
	}

	public int getBufferPos() {
		try {
			return RemoteConnection.getInstance().getInterface().getBufferPos();
		} catch (Throwable e) {
			connectService();
			return 0;
		}
	}

	public int getPreparingPercent() {
		try {
			return RemoteConnection.getInstance().getInterface().getPreparingPercent();
		} catch (Throwable e) {
			connectService();
			return 0;
		}
	}

	// 系统支持的音量范围0-xx
	public int getMaxVolume() {
		try {
			return RemoteConnection.getInstance().getInterface().getMaxVolume();
		} catch (Throwable e) {
			connectService();
			return 0;
		}
	}

	// 0到getMaxVolume()
	public int getVolume() {
		try {
			return RemoteConnection.getInstance().getInterface().getVolume();
		} catch (Throwable e) {
			connectService();
			return 0;
		}
	}

	// 0到getMaxVolume()
	public void setVolume(final int volume) {
		try {
			RemoteConnection.getInstance().getInterface().setVolume(volume);
		} catch (Throwable e) {
			connectService();
		}
	}

	public boolean isMute() {
		try {
			return RemoteConnection.getInstance().getInterface().isMute();
		} catch (Throwable e) {
			connectService();
			return false;
		}
	}

	public void setMute(final boolean mute) {
		try {
			RemoteConnection.getInstance().getInterface().setMute(mute);
		} catch (Throwable e) {
			connectService();
		}
	}
	
	// 当外部事件引起pause，调用这个接口之后，外部事件消失，播放不会自动continue
	public void setNoRecoverPause() {
		try {
			RemoteConnection.getInstance().getInterface().setNoRecoverPause();
		} catch (Throwable e) {
			connectService();
		}
	}
	
	public void updateVolume() {
		try {
			RemoteConnection.getInstance().getInterface().updateVolume();
		} catch (Throwable e) {
			connectService();
		}
	}
	
	public PlayLogInfo getPlayLogInfo() {
		try {
			return RemoteConnection.getInstance().getInterface().getPlayLogInfo();
		} catch (Throwable e) {
			//这里不要Assert和connectService
			return null;
		}
	}

	public void setSpectrumEnable (boolean enable) {
		try {
			RemoteConnection.getInstance().getInterface().setSpectrumEnable(enable);
		} catch (Throwable e) {
			connectService();
		}
	}

	//////////////////////
	public PlayProxy(final ThreadMessageHandler handler) {
		this.threadHandler = handler;
	}
	
	private boolean asyncRun(final MessageManager.Runner r) {
		return asyncRun(false, r);
	}

	private boolean asyncRun(final boolean forceSlow, final MessageManager.Runner r) {
		if (forceSlow) {
			if (System.currentTimeMillis() - lastMsgTime < 500) {
				return false;
			}
			lastMsgTime = System.currentTimeMillis();
		}
		MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(), r);
		return true;
	}

	
	private void connectService() {
		if (!RemoteConnection.getInstance().isConnected()) {
			RemoteConnection.getInstance().connect(null);
		}
	}

	public void setSpeed(float speed){
		try {
			RemoteConnection.getInstance().getInterface().setSpeed(speed);
		} catch (Throwable e) {
			connectService();
		}
	}
	public float getSpeed(){
		try {
			return RemoteConnection.getInstance().getInterface().getSpeed();
		} catch (Throwable e) {
			connectService();
			return .0f;
		}
	}

	public void clearPlayList() {
		try {
			RemoteConnection.getInstance().getInterface().clearPlayList();
		} catch (Throwable e) {
			connectService();
		}
	}
}
