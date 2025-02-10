package com.lazylite.media.remote.core.down;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.remote.core.play.AntiStealing;
import com.lazylite.media.remote.AIDLDownloadDelegate;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

//by haiping，不想跟系统的DownloaderManager重名，只好mgr了，别用错了哈
public final class DownloadMgr implements DownloadCore.OnTaskFinishedListener {
	private String TAG;

	public static void init(final ThreadMessageHandler handler) {
		threadHandler = handler;
		MessageManager.getInstance().syncRunTargetHandler(threadHandler.getHandler(),
				new MessageManager.Runner() {
					@Override
					public void call() {
						DownCacheMgr.init(handler);
						AntiStealing.init(handler);
					}
				});
	}

	private DownloadMgr(final DownloadProxy.DownGroup downGroup) {
		TAG = downGroup + "DownloadMgr";
		downloadCore = new DownloadCore(threadHandler, this, downGroup.toString());
		
		int size = DownloadProxy.DownType.values().length;
		taskGroups = new ArrayList<LinkedList<DownloadTask>>(size);
		for (int i = 0; i < size; i++) {
			taskGroups.add(null);
		}
	}

	// 这货不是单例！！这货不是单例！！！
	public static DownloadMgr getInstance(final DownloadProxy.DownGroup downGroup) {
		final int downGroupID = downGroup.ordinal();
		if (managers[downGroupID] == null) {
			MessageManager.getInstance().syncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
				@Override
				public void call() {
					managers[downGroupID] = new DownloadMgr(downGroup);
				}
			});
		}
		return managers[downGroupID];
	}


	/**
	 * 听书资源播放添加下载队列
	 * @param type
	 * @param delegate
	 * @param tarHandler
	 * @return
	 */
	// 这个接口其实线程安全。。。。
	public int addTask(final AudioInfo audioInfo, final DownloadProxy.DownType type,
					   final AIDLDownloadDelegate delegate,
					   final Handler tarHandler) {
		DownloadTask task = new DownloadTask();
		task.taskID = newTaskID();
		task.type = type;
		task.delegate = delegate;
		task.audioInfo = audioInfo;
		task.targetHandler = tarHandler;

		if (!TextUtils.isEmpty(audioInfo.resUrl)) {
			int index = audioInfo.resUrl.lastIndexOf(".");
			int lenth = audioInfo.resUrl.length();
			if (index < lenth && index >= 0) {
				task.format = audioInfo.resUrl.substring(index + 1);
			} else {
				task.format = "aac";
			}
		} else {
			task.format = "aac";
		}
		add(task);
		return task.taskID;
	}


	// 这个接口其实线程安全。。。。
	public int addTask(final String url, final String savePath,
                       final DownloadProxy.DownType type,
                       final AIDLDownloadDelegate delegate,
                       final Handler tarHandler) {
		DownloadTask task = new DownloadTask();
		task.taskID = newTaskID();
		task.type = DownloadProxy.DownType.FILE;
		task.delegate = delegate;
		task.url = url;
		task.savePath = savePath;
		task.targetHandler = tarHandler;
		Log.i(TAG, "addTask:" + url);

		add(task);
		return task.taskID;
	}

	// 这个接口其实也线程安全。。。。
	public static void removeTask(final long id) {
		MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				for (DownloadMgr mgr : managers) {
					if (mgr == null) {
						continue;
					}
					for (LinkedList<DownloadTask> taskGroup : mgr.taskGroups) {
						if (taskGroup == null) {
							continue;
						}
						for (DownloadTask task : taskGroup) {
							if (task.taskID == id) {
								if (task.running) {
									mgr.downloadCore.stop();
									mgr.schedule(10);
								}
								taskGroup.remove(task);
								return;
							}
						}
					}
				}
			}
		});
	}
	
	public static void stopNow() {
		MessageManager.getInstance().syncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				for (DownloadMgr mgr : managers) {
					if (mgr == null) {
						continue;
					}
					mgr.downloadCore.stop();
					
					for (int i = mgr.taskGroups.size() - 1; i >= 0; --i) {
						LinkedList<DownloadTask> group = mgr.taskGroups.get(i);
						if (group!=null) {
							group.clear();
						}
					}
				}
			}
		});
	}
	
	public static void stopAllExceptPlay() {
		MessageManager.getInstance().syncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				for (DownloadMgr mgr : managers) {
					if (mgr == null) {
						continue;
					}
					if (mgr != managers[DownloadProxy.DownGroup.MUSIC.ordinal()]) {
						mgr.downloadCore.stop();
					} else {
						DownloadProxy.DownType type = mgr.downloadCore.getCurrentDownType();
						if (type != DownloadProxy.DownType.PLAY && type != DownloadProxy.DownType.PREFETCH
								&& type != DownloadProxy.DownType.RADIO) {
							mgr.downloadCore.stop();
						}
					}

					for (int i = mgr.taskGroups.size() - 1; i >= 0; --i) {
						if (i == DownloadProxy.DownType.PLAY.ordinal()
								|| i == DownloadProxy.DownType.PREFETCH.ordinal()
								|| i == DownloadProxy.DownType.RADIO.ordinal()) {
							continue;
						}
						LinkedList<DownloadTask> group = mgr.taskGroups.get(i);
						if (group != null) {
							group.clear();
						}
					}
				}
			}
		});
	}

	private int newTaskID() {
		return nextTaskID.addAndGet(1);
	}




	private void addToFirst(final DownloadTask task) {
		MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				LinkedList<DownloadTask> group = getGroup(task.type);
				group.addFirst(task);
			}
		});

		schedule(0);
	}

	private void add(final DownloadTask task) {
		MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(), new MessageManager.Runner() {
			@Override
			public void call() {
				LinkedList<DownloadTask> group = getGroup(task.type);
				group.add(task);
			}
		});

		schedule(0);
	}

	private void schedule(final int delay) {
		Log.i(TAG, "schedule in");
		MessageManager.getInstance().asyncRunTargetHandler(threadHandler.getHandler(), delay, new MessageManager.Runner() {
			@Override
			public void call() {
				Log.i(TAG, "do schedule");
				for (int i = taskGroups.size() - 1; i >= 0; --i) {
					LinkedList<DownloadTask> group = taskGroups.get(i);
					if (group != null && group.size() > 0) {
						DownloadTask task = group.getFirst();
						if (!task.running) {
							downloadCore.stop();
							downloadCore.start(task);
						}
						return;
					}
				}
				Log.i(TAG, "no more task");
			}
		});
	}

	@Override
	public void onTaskFinished(final DownloadTask task) {
		Log.i(TAG, "onTaskFinished");
		LinkedList<DownloadTask> group = taskGroups.get(task.type.ordinal());
		group.remove(task);
		schedule(0);
	}

	private LinkedList<DownloadTask> getGroup(final DownloadProxy.DownType type) {
		int typeID = type.ordinal();
		LinkedList<DownloadTask> list = taskGroups.get(typeID);
		if (list == null) {
			list = new LinkedList<DownloadTask>();
			taskGroups.set(typeID, list);
		}
		return list;
	}

	////////
	private ArrayList<LinkedList<DownloadTask>> taskGroups;
	private DownloadCore downloadCore;

	private static DownloadMgr[] managers = new DownloadMgr[DownloadProxy.DownGroup.values().length];
	private static ThreadMessageHandler threadHandler;
	private static AtomicInteger nextTaskID = new AtomicInteger(1001);

}
