package com.lazylite.media.remote.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lazylite.media.remote.AIDLRemoteInterfaceImpl;
import com.lazylite.media.remote.core.down.DownloadMgr;
import com.lazylite.media.remote.core.play.PlayManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.play.notification.PlayNotificationMgrImpl;

import timber.log.Timber;

public final class RemoteService extends Service {

    public static final String TAG = "RemoteService";

    private static ThreadMessageHandler playThreadHandler;
    private static ThreadMessageHandler downloadThreadHandler;
    private static AIDLRemoteInterfaceImpl remoteInterface;


    @Override
    public void onCreate() {
        super.onCreate();
        Timber.tag("ServiceMgr").e("RemoteService onCreate");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true); // 移除前台进程用通知栏图标，同时可杀掉本服务
        PlayNotificationMgrImpl.getInstance().clearAllNotification();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return remoteInterface;
    }

    private void init() {
        if (playThreadHandler == null) {
            playThreadHandler = new ThreadMessageHandler("play");
            PlayManager.getInstance().init(playThreadHandler);
        }

        if (downloadThreadHandler == null) {
            downloadThreadHandler = new ThreadMessageHandler("download");
            DownloadMgr.init(downloadThreadHandler);
        }

        if (remoteInterface == null) {
            remoteInterface = new AIDLRemoteInterfaceImpl(playThreadHandler);
        }

    }
}
