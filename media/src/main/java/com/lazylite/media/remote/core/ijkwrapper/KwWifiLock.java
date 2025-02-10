package com.lazylite.media.remote.core.ijkwrapper;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import com.lazylite.media.Media;
import com.lazylite.mod.messagemgr.MessageManager;

// by haiping

public final class KwWifiLock {
	public static synchronized void lock() {
		createWifiLock();
		tarIsRelease = false;
		if (!locker.isHeld()) {
			try {
				locker.acquire();
			} catch (Throwable e) {}
		}
	}

	public static synchronized void unLock() {
		createWifiLock();
		tarIsRelease = true;
		if (countDown) {
			return;
		}
		countDown = true;
		MessageManager.getInstance().asyncRun(60*1000, new MessageManager.Runner() {
			@Override
			public void call() {
				synchronized (KwWifiLock.class) {
					countDown = false;
					if (tarIsRelease && locker.isHeld()) {
						try {
							locker.release();
						} catch (Throwable e) {}
					}
				}
			}
		});
	}
	
	public static void createWifiLock() {
		if (locker == null) {
			try {
				WifiManager wifiService = (WifiManager) Media.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				locker = wifiService.createWifiLock("com.lazylite.player");
				locker.setReferenceCounted(false);
			} catch (Throwable e) {}
		}
    }  
	
	private static WifiLock locker;
	private static boolean countDown;
	private static boolean tarIsRelease;
}

