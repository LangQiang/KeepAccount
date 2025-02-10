package com.lazylite.media.remote.service;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.lazylite.media.Media;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.utils.KwTrigger;

import timber.log.Timber;

public class ServiceMgr implements ServiceConnection {
	private static final String TAG = "ServiceMgr";

	public static void connect(final ConnectListener listener) {
		LogMgr.d(TAG,"ServiceMgr.connect");
		Timber.tag("ServiceMgr").e("000");
		if (status != ConnectStatus.NO_CONNECT) {
			return;
		}
		
		bindTrigger = new KwTrigger(2, () -> {
			setConnected();
			if (listener!=null){
				listener.onConnected();
			}
		});
		Timber.tag("ServiceMgr").e("111");


		if (RemoteConnection.getInstance().isConnected()) {
			bindTrigger.touch();
		} else {
			RemoteConnection.getInstance().connect(() -> bindTrigger.touch());
		}
		
		Context ctx = Media.getContext().getApplicationContext();
		Intent intent2 = new Intent(ctx, LocalService.class);
		intent2.putExtra("fromlocal", true);
		try {
			startForegroundServiceCompat(ctx, intent2);
		} catch (Exception exception){
			// 权限失败捕获
		}
		if (ctx.bindService(intent2, instance, Context.BIND_AUTO_CREATE)) {
			status = ConnectStatus.BINDING;
		} else {
			// 失败了怎么办？怎么办都是自欺欺人，先让它能进主界面再说吧
			bindTrigger.touch();
		}
	}

	public static void startForegroundServiceCompat(Context context, Intent intent) {
		if (context != null && intent != null) {
			if (DeviceInfo.isAndroidO()) {
				startForegroundServiceSafely(context, intent);
			} else {
				startServiceSafely(context, intent);
			}
		}
	}

	public static void startServiceSafely(Context context, Intent intent) {
		if (context != null && intent != null) {
			try {
				context.startService(intent);
			} catch (Exception ignored) {
				LogMgr.e("RemoteConnection", "startService failed, intent = " + intent);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.O)
	private static void startForegroundServiceSafely(Context context, Intent intent) {
		if (context != null && intent != null) {
			try {
				context.startForegroundService(intent);
			} catch (Exception ignored) {
				LogMgr.e("RemoteConnection", "startForegroundService failed, intent = " + intent);
			}
		}
	}


	/**
	 * 更新服务连接成功后的状态消息
	 */
	public static void setConnected() {
		status = ConnectStatus.CONNECTED;
	}

	public static void disconnect() {
		
		//退出程序stopService 
		Context ctx = Media.getContext().getApplicationContext();
		Intent intent = new Intent(ctx, LocalService.class);
		ctx.stopService(intent);
		
		if (status == ConnectStatus.NO_CONNECT) {
			return;
		}
		status = ConnectStatus.NO_CONNECT;
	
		try {
			RemoteConnection.getInstance().getInterface().killYourself();
		} catch (Throwable e) {
			LogMgr.e(TAG, e);
		}	
		
		RemoteConnection.getInstance().disconnect();
		ctx.unbindService(instance);
	}

	public static void unBind(){
		if (status == ConnectStatus.NO_CONNECT) {
			return;
		}
		status = ConnectStatus.NO_CONNECT;

		RemoteConnection.getInstance().unBind();

		Context ctx = Media.getContext().getApplicationContext();
		ctx.unbindService(instance);
	}

	public static boolean isBinding() {
		return status == ConnectStatus.BINDING;
	}

	public static boolean isConnected() {
		return status == ConnectStatus.CONNECTED;
	}

	@Override
	public void onServiceConnected(final ComponentName name, final IBinder service) {
		LogMgr.d("playProxy","ServiceMgr.onServiceConnected");
		if(bindTrigger!=null){
			bindTrigger.touch();
		}
	}
	
	@Override
	public void onServiceDisconnected(final ComponentName name) {
		status = ConnectStatus.NO_CONNECT;
		LogMgr.d("playProxy","ServiceMgr.onServiceDisconnected");
	}
	
	private enum ConnectStatus {
		NO_CONNECT, BINDING, CONNECTED
	}

	private static final ServiceMgr instance = new ServiceMgr();
	private static ConnectStatus status = ConnectStatus.NO_CONNECT;
	private static  KwTrigger bindTrigger;

}
