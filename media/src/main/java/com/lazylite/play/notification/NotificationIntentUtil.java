package com.lazylite.play.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

/**
 * @author qyh
 * @date 2022/1/18
 * describe:
 */
public class NotificationIntentUtil {

    public static PendingIntent buildIntent(int notifityMode, Context context) {
        try {
            Intent intent = null;
            switch (notifityMode) {
                case PlayNotificationMgrImpl.PENDING_INTENT_GO_APP:
                    intent = new Intent("com.lazy.lite.splash.EntryActivity");
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    intent.setAction("android.intent.action.MAIN");
                    return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_MUTABLE);
                case PlayNotificationMgrImpl.PENDING_INTENT_PRE:
                    intent = new Intent(PlayNotificationReceiver.RECEIVER_PLAY_PRE);
                    return PendingIntent.getBroadcast(context, 2, intent, PendingIntent.FLAG_MUTABLE);
                case PlayNotificationMgrImpl.PENDING_INTENT_NEXT:
                    intent = new Intent(PlayNotificationReceiver.RECEIVER_PLAY_NEXT);
                    return PendingIntent.getBroadcast(context, 3, intent, PendingIntent.FLAG_MUTABLE);
                case PlayNotificationMgrImpl.PENDING_INTENT_PALYING:
                    intent = new Intent(PlayNotificationReceiver.RECEIVER_PLAY_PLAYING);
                    return PendingIntent.getBroadcast(context, 4, intent, PendingIntent.FLAG_MUTABLE);
                case PlayNotificationMgrImpl.PENDING_INTENT_APP_EXIT:
                    intent = new Intent(PlayNotificationReceiver.RECEIVER_EXIT);
                    return PendingIntent.getBroadcast(context, 5, intent, PendingIntent.FLAG_MUTABLE);
                default:
            }
        } catch (Exception ignored) {
            Timber.e(ignored);
        }
        return null;
    }
}
