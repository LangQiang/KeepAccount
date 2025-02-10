package com.lazylite.media.remote.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.PowerManager;

import com.godq.media_api.media.IPlayObserver;
import com.lazylite.media.remote.AIDLLocalInterfaceImpl;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.play.notification.PlayNotificationMgrImpl;
import com.lazylite.play.notification.PlayNotificationReceiver;

public class LocalService extends Service {
	private static final String TAG = "MainService";

	@Override
	public final void onCreate() {
		super.onCreate();

		PlayNotificationMgrImpl.getInstance().clearAllNotification();

		MessageManager.getInstance().attachMessage(IPlayObserver.EVENT_ID, playControlObserver);

		if (localInterface == null) {
			localInterface = new AIDLLocalInterfaceImpl();
		}
		PlayNotificationReceiver.registerNotificationReceiver(this);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PlayNotificationMgrImpl.getInstance().startServiceNotify(this);
		return START_STICKY;
	}

	@Override
	public final void onDestroy() {
		MessageManager.getInstance().detachMessage(IPlayObserver.EVENT_ID, playControlObserver);

		RemoteConnection.getInstance().disconnect();

		PlayNotificationReceiver.unregisterNotificationReceiver(this);
		// 移除前台进程,移除通知栏
		stopForeground(true);
		// 注释原因：oppo反馈在他们Android11高端手机上播歌时无法移除通知栏
		// ModMgr.getNotificationMgr().clearAllNotification();
	}

	@Override
	public final IBinder onBind(final Intent intent) {
//		AIDLRemoteInterface remoteInterface = RemoteConnection.getInstance().getInterface();
//		if (remoteInterface != null) {
//			boolean fromLocal = intent.getBooleanExtra("fromlocal", false);
//			if (!fromLocal) {
//				try {
//					remoteInterface.onConnect();
//				} catch (Throwable e) {
//					LogMgr.e(TAG, e);
//				}
//			}
//		} else {
//			RemoteConnection.getInstance().connect(null);
//		}
		if (!ServiceMgr.isConnected()) {
			ServiceMgr.connect(null);
		}
		return localInterface;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


	private final IPlayObserver playControlObserver = new IPlayObserver() {
		@Override
		public void onPlay() {
			getToWork();
		}

		@Override
		public void onPause() {
			haveARest();
		}

		@Override
		public void onContinue() {
			getToWork();
		}

		@Override
		public void onFailed(int errCode, String msg) {
			haveARest();
		}

		@Override
		public void onStop(boolean end) {
			haveARest();
		}

	};

	private static AIDLLocalInterfaceImpl localInterface;

	private PowerManager.WakeLock mWakelock;
	private void haveARest() {
		LogMgr.d(TAG, "haveARest");
		createWakeLock();
		if (mWakelock != null && mWakelock.isHeld()) {
			try {
				mWakelock.release();
			} catch (Throwable ignore) {
			}
		}
	}

	private void getToWork() {
		LogMgr.d(TAG, "getToWork");
		createWakeLock();
		if (mWakelock != null && !mWakelock.isHeld()) {
			try {
				mWakelock.acquire();
			} catch (Throwable ignore) {
			}
		}
	}

	private void createWakeLock() {
		if (mWakelock == null) {
			try {
				PowerManager powerMan = (PowerManager) getSystemService(Context.POWER_SERVICE);
				mWakelock = powerMan.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
				mWakelock.setReferenceCounted(false);
			} catch (Throwable e) {
				mWakelock = null;
			}
		}
	}
}
