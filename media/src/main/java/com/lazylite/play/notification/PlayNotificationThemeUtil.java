package com.lazylite.play.notification;

import android.content.Context;
import android.graphics.Color;
import android.widget.RemoteViews;

import com.lazylite.media.R;
import com.lazylite.mod.App;
import com.lazylite.mod.utils.DeviceInfo;

/**
 * @author qyh
 * @date 2022/1/19
 * describe:播放通知栏背景适配、图标
 */
public class PlayNotificationThemeUtil {
    private static final int COLOR_TYPE_WHITE = 0;
    private static final int COLOR_TYPE_BLACK = 1;
    private static Context mContext;

    public static void setViewTheme(Context context, RemoteViews remoteViews, boolean isBig, int type) {
        mContext = context;
        boolean isBgDark = NotificationUtil.isBgDarkTheme(context); //背景取色判断
        boolean isSysDark = NotificationUtil.isSysDark();

        if (isSysDark) {
            //适配1：oppo 系统暗色模式 androidQ -> 文字 oppo系统会取反 图片要自己设置
            if (DeviceInfo.isAndroidQ() && DeviceInfo.isOPPO()) {
                setTextTheme(remoteViews, isBig, COLOR_TYPE_BLACK);
                setIconTheme(remoteViews, isBig, COLOR_TYPE_WHITE, type);
                return;
            }
            //适配2：华为 vivo samsung 会把全部控件颜色取反 要设置黑色的文字和icon
            if (DeviceInfo.isHuaWei()
                    || (DeviceInfo.isAndroidQ() && DeviceInfo.isViVo())
                    || (DeviceInfo.isAndroidQ() && DeviceInfo.isSamsung())) {
                setTextTheme(remoteViews, isBig, COLOR_TYPE_BLACK);
                setIconTheme(remoteViews, isBig, COLOR_TYPE_BLACK, type);
                return;
            }
            //小米和其他手机 按颜色手动设置
            setTextTheme(remoteViews, isBig, COLOR_TYPE_WHITE);
            setIconTheme(remoteViews, isBig, COLOR_TYPE_WHITE, type);
        } else {
            if (isBgDark) {
                setTextTheme(remoteViews, isBig, COLOR_TYPE_WHITE);
                setIconTheme(remoteViews, isBig, COLOR_TYPE_WHITE, type);
            } else {
                setTextTheme(remoteViews, isBig, COLOR_TYPE_BLACK);
                setIconTheme(remoteViews, isBig, COLOR_TYPE_BLACK, type);
            }
        }
    }

    private static void setTextTheme(RemoteViews remoteViews, boolean isBig, int colorTheme) {
        if (colorTheme == COLOR_TYPE_WHITE) {
            remoteViews.setInt(R.id.trackname, "setTextColor", App.getInstance().getResources().getColor(R.color.white));
            remoteViews.setInt(R.id.artistalbum, "setTextColor", App.getInstance().getResources().getColor(R.color.white70));
        } else {
            remoteViews.setInt(R.id.trackname, "setTextColor", App.getInstance().getResources().getColor(R.color.black80));
            remoteViews.setInt(R.id.artistalbum, "setTextColor", App.getInstance().getResources().getColor(R.color.black40));
        }
    }

    private static void setIconTheme(RemoteViews remoteViews, boolean isBig, int colorTheme, int type) {
        int status = type;
        if (colorTheme == COLOR_TYPE_WHITE) {
            remoteViews.setImageViewResource(R.id.notification_exit_button, R.drawable.media_notify_close_white);
            remoteViews.setImageViewResource(R.id.next, R.drawable.media_notify_next_white);
            if (isBig) {
                remoteViews.setImageViewResource(R.id.prev, R.drawable.media_notify_pre_white);
            }
            if (PlayNotificationMgrImpl.NOTIFY_TYPE_PLAY == status) {
                remoteViews.setImageViewResource(R.id.playpause, R.drawable.media_notify_pause_white);
            } else {
                remoteViews.setImageViewResource(R.id.playpause, R.drawable.media_notify_play_white);
            }
        } else {
            if (DeviceInfo.getRomType(mContext) == DeviceInfo.XIAOMI) {//解决小米机型通知栏中间白色背景问题
                remoteViews.setInt(R.id.notificationbg, "setBackgroundColor", Color.TRANSPARENT);
            } else {
                remoteViews.setInt(R.id.notificationbg, "setBackgroundColor", Color.WHITE);
            }
            remoteViews.setImageViewResource(R.id.notification_exit_button, R.drawable.media_notify_close_black);
            remoteViews.setImageViewResource(R.id.next, R.drawable.media_notify_next_black);
            if (isBig) {
                remoteViews.setImageViewResource(R.id.prev, R.drawable.media_notify_pre_black);
            }
            if (PlayNotificationMgrImpl.NOTIFY_TYPE_PLAY == status) {
                remoteViews.setImageViewResource(R.id.playpause, R.drawable.media_notify_pause_black);
            } else {
                remoteViews.setImageViewResource(R.id.playpause, R.drawable.media_notify_play_black);
            }
        }
    }
}
