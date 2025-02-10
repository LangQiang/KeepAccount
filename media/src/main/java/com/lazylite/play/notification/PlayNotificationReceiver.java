package com.lazylite.play.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.godq.media_api.media.IPlayController;
import com.godq.media_api.media.PlayStatus;
import com.lazylite.media.BuildConfig;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.media.remote.service.RemoteConnection;
import com.lazylite.mod.App;
import com.lazylite.mod.messagemgr.MessageManager;

/**
 * @author qyh
 * @date 2022/1/19
 * describe:
 */
public class PlayNotificationReceiver extends BroadcastReceiver {

    private static PlayNotificationReceiver playNotificationReceiver;

    public static final String RECEIVER_PLAY_PRE = App.getInstance().getPackageName() + ".play.pre";
    public static final String RECEIVER_PLAY_NEXT = App.getInstance().getPackageName() + ".play.next";
    public static final String RECEIVER_PLAY_PLAYING = App.getInstance().getPackageName() + ".play.playing";
    public static final String RECEIVER_EXIT = App.getInstance().getPackageName() + ".app.exit";

    public static void registerNotificationReceiver(Context context) {
        if (playNotificationReceiver == null) {
            playNotificationReceiver = new PlayNotificationReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PlayNotificationReceiver.RECEIVER_PLAY_NEXT);
            intentFilter.addAction(PlayNotificationReceiver.RECEIVER_PLAY_PRE);
            intentFilter.addAction(PlayNotificationReceiver.RECEIVER_PLAY_PLAYING);
            intentFilter.addAction(PlayNotificationReceiver.RECEIVER_EXIT);
            context.registerReceiver(playNotificationReceiver, intentFilter);
        }
    }

    public static void unregisterNotificationReceiver(Context ctx) {
        if (playNotificationReceiver != null) {
            try {
                ctx.unregisterReceiver(playNotificationReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(RECEIVER_PLAY_NEXT)) {
            PlayControllerImpl.getInstance().playNext(true);
        } else if (action.equals(RECEIVER_PLAY_PLAYING)) {
            IPlayController instance = PlayControllerImpl.getInstance();
            if (instance.getPlayStatus() == PlayStatus.STATUS_PLAYING) {
                instance.pause();
            } else {
                instance.continuePlay();
            }
        } else if (action.equals(RECEIVER_PLAY_PRE)) {
            PlayControllerImpl.getInstance().playPre(true);
        }else if(action.equals(RECEIVER_EXIT)){
            exitApp();
        }
    }

    private void exitApp() {
        MessageManager.getInstance().asyncRun(500, new MessageManager.Runner() {
            @Override
            public void call() {
                RemoteConnection.getInstance().disconnect();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });
    }
}
