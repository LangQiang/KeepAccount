package com.lazylite.media.remote.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.lazylite.media.Media;
import com.lazylite.media.remote.AIDLPlayDelegate;
import com.lazylite.media.remote.AIDLPlayDelegateImpl;
import com.lazylite.media.remote.AIDLRemoteInterface;
import com.lazylite.media.remote.core.ijkwrapper.PlayDelegate;
import com.lazylite.mod.App;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.utils.KwDebug;
import com.lazylite.mod.utils.KwTrigger;

import java.util.ArrayList;

import timber.log.Timber;


public class RemoteConnection implements ServiceConnection {

	private static final RemoteConnection instance = new RemoteConnection();

	private AIDLRemoteInterface remoteInterface;

	private AIDLPlayDelegateImpl aidlPlayDelegate;

	private final ArrayList<ConnectListener> listeners = new ArrayList<>();

	private IBinder service;

	private ConnectStatus status = ConnectStatus.NO_CONNECT;

	private KwTrigger bindTrigger;

	public void unBind() {

	}

	private enum ConnectStatus {
		NO_CONNECT, BINDING, CONNECTED
	}
	protected AIDLPlayDelegate playDelegate;


	public RemoteConnection() {
	}
	
	public static RemoteConnection getInstance() {
		return instance;
	}
	
	public AIDLRemoteInterface getInterface() {
		return remoteInterface;
	}

	protected Class<?> getServiceClass() {
		return RemoteService.class;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		this.service = service;
		onConnected(service);
		if(bindTrigger!=null) {
			bindTrigger.touch();
		}
	}

	private void onConnected(IBinder service) {
		remoteInterface = AIDLRemoteInterface.Stub.asInterface(service);
		if (aidlPlayDelegate == null) {
			aidlPlayDelegate = new AIDLPlayDelegateImpl();
		}
		setPlayDelegate(aidlPlayDelegate);
		try {
			remoteInterface.setDelegate(aidlPlayDelegate);
			remoteInterface.onConnect();
		} catch (Throwable e) {
		}
	}

	private void onDisconnected() {
		remoteInterface = null;

		MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
			@Override
			public void call() {
				connect(() -> {
					try {
						playDelegate.PlayDelegate_Failed(PlayDelegate.ErrorCode.SERVICEREST.ordinal());
					} catch (Throwable e) {}
				});
			}
		});
	}
	
	public void onConnectCallback() {
		if (bindTrigger != null) {
			bindTrigger.touch();
		}
	}


	public void connect(final ConnectListener listener) {
		Timber.tag("ServiceMgr").e("RemoteConnection connect");
		if (listener != null) {
			listeners.add(listener);
		}
		if (status == ConnectStatus.BINDING) {
			return;
		}
		if (bindTrigger == null) {
			bindTrigger = new KwTrigger(1, new KwTrigger.Listener() {
				@Override
				public void trigger() {
					status = ConnectStatus.CONNECTED;
					while (!listeners.isEmpty()) {
						ConnectListener l = listeners.get(listeners.size()-1);
						l.onConnected();
						listeners.remove(l);
					}
					bindTrigger = null;
				}
			});
		}

		if (service != null) {
			bindTrigger.immediately();
			return;
		}

		try {
			Context ctx = App.getInstance();
			Intent intent = new Intent(ctx, getServiceClass());
			ctx.startService(intent);
			status = ConnectStatus.BINDING;
			if (!ctx.bindService(intent, this, Context.BIND_AUTO_CREATE)) {
				// 自欺欺人…又能如何
				bindTrigger.immediately();
			}
			Timber.tag("ServiceMgr").e("RemoteService start");
		} catch (Exception e) { //可能进程还未启动
			Timber.tag("ServiceMgr").e(e);
		}
	}



	@Override
	public final void onServiceDisconnected(final ComponentName name) {
		status = ConnectStatus.NO_CONNECT;
		service = null;
		onDisconnected();
	}

	public void setPlayDelegate(final AIDLPlayDelegate d) {
		playDelegate = d;
	}

	public final boolean isConnected() {
		return service != null;
	}
	public final void disconnect() {
		if (status == ConnectStatus.NO_CONNECT) {
			return;
		}
		status = ConnectStatus.NO_CONNECT;
		try {
			RemoteConnection.getInstance().getInterface().killYourself();
		} catch (Throwable e) {
		}
        Context ctx = Media.getContext().getApplicationContext();
        try {
			ctx.unbindService(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(ctx, getServiceClass());
		ctx.stopService(intent);
	}

}
