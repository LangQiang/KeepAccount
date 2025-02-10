package com.lazylite.play.notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.basemodule.R;
import com.lazylite.mod.App;
import com.lazylite.mod.utils.DeviceInfo;

public class ChannelUtil {

    public static void buildChannelId(NotificationCompat.Builder builder, NotificationManager notificationManager) {
        if (DeviceInfo.isAndroidO()) {
            Context context = App.getInstance().getApplicationContext();
            builder.setChannelId(getChannelId(context, notificationManager));
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static String getChannelId(Context context, NotificationManager notificationManager) {
        String channelId = context.getPackageName();
        String name = context.getString(R.string.app_name);
        // importance：high啥都有；default有提示音，没有横幅提示；low没有提示音也没有横幅提示，但是级别低了不确定会不会被顶掉
        NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_LOW);
        // 默认不显示桌标圆角标志
        channel.setShowBadge(false);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
        return channelId;
    }
}
