package com.lazylite.play.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.godq.media_api.media.IPlayObserver;
import com.lazylite.media.Media;
import com.lazylite.media.R;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.mod.App;
import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper;
import com.lazylite.mod.imageloader.fresco.listener.SimpleDownloaderListener;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.utils.BitmapUtil;
import com.lazylite.mod.utils.DeviceInfo;

/**
 * @author qyh
 * describe:
 */
public class PlayNotificationMgrImpl {

    public static final int PENDING_INTENT_GO_APP = 0x010705;
    public static final int PENDING_INTENT_PRE = 0x010706;
    public static final int PENDING_INTENT_NEXT = 0x010707;
    public static final int PENDING_INTENT_PALYING = 0x010708;
    public static final int PENDING_INTENT_APP_EXIT = 0x01070F;
    private static final int NOTIFICATION_ID_PLAY = 0x010703;

    public static final int NOTIFY_TYPE_PAUSE = 0; //
    public static final int NOTIFY_TYPE_PLAY = 1; //

    public static final String CHANNEL_ID_PLAYCONTROL = "TmePlayControlChannel";
    private final Context mContext;
    private final NotificationManager mNotificationManager;

    private final String defaultTicker;
    private final String defaultStrDef;
    private final String defaultArtistDef;

    private PlayNotificationMgrImpl() {
        Log.e("test", "isMain:" + App.isMainProcess());
        mContext = Media.getContext().getApplicationContext();
        defaultTicker = mContext.getString(R.string.app_name);
        defaultStrDef = mContext.getString(R.string.app_name);
        defaultArtistDef = "每一个创作者都会发光";
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        MessageManager.getInstance().attachMessage(IPlayObserver.EVENT_ID, playControlObserver);
    }

    private static class Inner {
        private static final PlayNotificationMgrImpl INSTANCE = new PlayNotificationMgrImpl();
    }

    public static PlayNotificationMgrImpl getInstance() {
        return Inner.INSTANCE;
    }

    public void startServiceNotify(Service service) {
        if (service != null) {
            Notification notification;
            notification = getPlayNotification(
                    mContext,
                    mNotificationManager,
                    BitmapUtil.getBitmap(mContext.getResources(), R.drawable.base_logo),
                    defaultStrDef,
                    defaultArtistDef,
                    defaultTicker,
                    NOTIFY_TYPE_PAUSE);
            if (notification == null) {
                return;
            }
            try {
                service.startForeground(NOTIFICATION_ID_PLAY, notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearAllNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_ID_PLAY);
        }
    }


    private final IPlayObserver playControlObserver = new IPlayObserver() {

        @Override
        public void onRealPlay() {
            LogMgr.e("notify", "realPlay");
            notifyNormalInfo(NOTIFY_TYPE_PLAY);
        }

        @Override
        public void onPlay() {
            LogMgr.e("notify", "onPlay");
            notifyNormalInfo(NOTIFY_TYPE_PLAY);
            getCoverBeforeNotify();
        }



        @Override
        public void onStop(boolean end) {
            LogMgr.e("notify", "onStop");
            notifyNormalInfo(NOTIFY_TYPE_PAUSE);
        }

        @Override
        public void onFailed(int errCode, String msg) {
            LogMgr.e("notify", "onFailed");
            notifyNormalInfo(NOTIFY_TYPE_PAUSE);
        }

        @Override
        public void onContinue() {
            LogMgr.e("notify", "onContinue");
            notifyNormalInfo(NOTIFY_TYPE_PLAY);
        }

        @Override
        public void onPause() {
            LogMgr.e("notify", "onPause");
            notifyNormalInfo(NOTIFY_TYPE_PAUSE);
        }

        @Override
        public void clearPlayList() {
            LogMgr.e("notify", "clearPlayList");

            notifyNormalInfo(NOTIFY_TYPE_PAUSE);
        }
    };

    private final PlayNotifyContent lastPlayContent = new PlayNotifyContent();

    private void getCoverBeforeNotify() {
        Bitmap bitmap = getBitmapCache();
        if (bitmap != null) {
            notifyBitmap(bitmap);
        } else {
            String tempUrl = lastPlayContent.musicImg;
            ImageLoaderWapper.getInstance().load(tempUrl,
                    new SimpleDownloaderListener() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            try {
                                if (lastPlayContent.musicImg != null && lastPlayContent.musicImg.equals(tempUrl)) {
                                    lastPlayContent.bitmapCache = new Pair<>(lastPlayContent.musicImg, bitmap);
                                    notifyBitmap(bitmap);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                        }
                    });
        }
    }

    private Bitmap getBitmapCache() {
        if (lastPlayContent.bitmapCache == null) {
            return null;
        }
        if (lastPlayContent.bitmapCache.first != null && lastPlayContent.bitmapCache.first.equals(lastPlayContent.musicImg)) {
            return lastPlayContent.bitmapCache.second;
        }
        return null;
    }

    private void notifyBitmap(Bitmap bitmap) {
        realNotify(lastPlayContent, bitmap);
    }

    private void notifyNormalInfo(int type) {
        ChapterBean currentChapter = PlayControllerImpl.getInstance().getCurrentChapter();
        BookBean bookBean = PlayControllerImpl.getInstance().getCurrentBook();
        if (currentChapter == null || bookBean == null) {
            lastPlayContent.musicArtist = defaultArtistDef;
            lastPlayContent.musicImg = "";
            lastPlayContent.musicName = defaultStrDef;
            lastPlayContent.type = type;
            realNotify(lastPlayContent, BitmapUtil.getBitmap(mContext.getResources(), R.drawable.base_logo));
        } else {
            lastPlayContent.musicArtist = TextUtils.isEmpty(currentChapter.mArtist) ? bookBean.mArtist : currentChapter.mArtist;
            lastPlayContent.musicName = currentChapter.mName;
            lastPlayContent.type = type;
            lastPlayContent.musicImg = bookBean.mImgUrl;
            realNotify(lastPlayContent, null);
        }
    }

    private void realNotify(PlayNotifyContent playNotifyContent, Bitmap bitmap) {
        if (mNotificationManager != null && playNotifyContent != null) {
            Notification notification = getPlayNotification(mContext, mNotificationManager, bitmap, playNotifyContent.musicName,
                    playNotifyContent.musicArtist, defaultTicker, playNotifyContent.type);
            mNotificationManager.notify(NOTIFICATION_ID_PLAY, notification);
        }
    }


    @SuppressLint("ObsoleteSdkInt")
    private static Notification getPlayNotification(
            Context mContext, NotificationManager mNotificationManager,
            Bitmap musicPic, String musicTitle, String musicArtist, String ticker, int type) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        PendingIntent contentIntent = NotificationIntentUtil.buildIntent(PENDING_INTENT_GO_APP, mContext);
        PendingIntent preIntent = NotificationIntentUtil.buildIntent(PENDING_INTENT_PRE, mContext);
        PendingIntent nextIntent = NotificationIntentUtil.buildIntent(PENDING_INTENT_NEXT, mContext);
        PendingIntent playPauseIntent = NotificationIntentUtil.buildIntent(PENDING_INTENT_PALYING, mContext);
        PendingIntent exitIntent = NotificationIntentUtil.buildIntent(PENDING_INTENT_APP_EXIT, mContext);

        builder.setSmallIcon(R.drawable.base_img_default);
        builder.setContentTitle(musicTitle);
        builder.setContentText(musicArtist);
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker);
        }
//        builder.setContentIntent(contentIntent);
        builder.setOngoing(true);
        ChannelUtil.buildChannelId(builder, mNotificationManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }
        builder.setGroup(CHANNEL_ID_PLAYCONTROL);

        RemoteViews smallRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.media_statusbar);
        if (musicPic == null || musicPic.isRecycled()) {
//            smallRemoteViews.setImageViewBitmap(R.id.albumart, BitmapUtil.getBitmap(mContext.getResources(), R.drawable.base_logo));
        } else {
            smallRemoteViews.setImageViewBitmap(R.id.albumart, DeviceInfo.isNeedCopyNotificationBitmap() ? musicPic.copy(musicPic.getConfig(), musicPic.isMutable()) : musicPic);
        }
        smallRemoteViews.setTextViewText(R.id.trackname, musicTitle);
        smallRemoteViews.setTextViewText(R.id.artistalbum, musicArtist);

        PlayNotificationThemeUtil.setViewTheme(mContext, smallRemoteViews, false, type);

        smallRemoteViews.setOnClickPendingIntent(R.id.playpause, playPauseIntent);
        smallRemoteViews.setOnClickPendingIntent(R.id.next, nextIntent);
        smallRemoteViews.setOnClickPendingIntent(R.id.notification_exit_button, exitIntent);
        builder.setContent(smallRemoteViews);
        builder.setContentIntent(contentIntent);

        Notification notification;
        try {
            notification = builder.build();
        } catch (Exception e) {
            return null;
        }


        RemoteViews bigContentView = new RemoteViews(mContext.getPackageName(), R.layout.media_big_statusbar);
        if (musicPic == null || musicPic.isRecycled()) {
//            bigContentView.setImageViewBitmap(R.id.albumart, BitmapUtil.getBitmap(mContext.getResources(), R.drawable.base_logo));
        } else {
            bigContentView.setImageViewBitmap(R.id.albumart, DeviceInfo.isNeedCopyNotificationBitmap() ? musicPic.copy(musicPic.getConfig(), musicPic.isMutable()) : musicPic);
        }
        bigContentView.setTextViewText(R.id.trackname, musicTitle);
        bigContentView.setTextViewText(R.id.artistalbum, musicArtist);

        bigContentView.setOnClickPendingIntent(R.id.prev, preIntent);
        bigContentView.setOnClickPendingIntent(R.id.playpause, playPauseIntent);
        bigContentView.setOnClickPendingIntent(R.id.next, nextIntent);
        bigContentView.setOnClickPendingIntent(R.id.notification_exit_button, exitIntent);

        PlayNotificationThemeUtil.setViewTheme(mContext, bigContentView, true, type);

        notification.bigContentView = bigContentView;
        notification.contentIntent = contentIntent;
        return notification;
    }


    public static void initNotificationMusic(Context context, String imgUrl, String name, String artist) {
        ImageLoaderWapper.getInstance().load(imgUrl,
                new SimpleDownloaderListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        try {
                            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            if (mNotificationManager != null) {
                                Notification notification = getPlayNotification(context, mNotificationManager, bitmap, name,
                                        artist, context.getString(R.string.app_name), NOTIFY_TYPE_PAUSE);
                                mNotificationManager.notify(NOTIFICATION_ID_PLAY, notification);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
    }
}
