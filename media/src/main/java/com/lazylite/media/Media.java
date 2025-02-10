package com.lazylite.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.lazylite.annotationlib.AutoInit;
import com.lazylite.bridge.init.Init;
import com.godq.media_api.media.IMediaService;
import com.godq.media_api.media.IPlayController;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.media.remote.service.ServiceMgr;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.utils.ApplicationUtils;
import com.lazylite.play.templist.TempPlayListManager;

import timber.log.Timber;

@AutoInit
public class Media extends Init {

    public static final String TAG = "MEDIA_LOG";

    private static boolean isInit;

    public static Handler mainHandler = new Handler(Looper.getMainLooper());

    @SuppressLint("StaticFieldLeak")
    private static Context app; //appContext 忽略泄漏提示

    @Override
    public void init(Context context) {
        app = context.getApplicationContext();
        MessageManager.getInstance().asyncRun(2000, new MessageManager.Runner() {
            @Override
            public void call() {
                initSelf(context);

            }
        });
    }

    @Override
    public void initAfterAgreeProtocol(Context context) {
        initSelf(context);
    }

    private void initSelf(Context context) {
        if (isInit) {
            return;
        }

        isInit = true;
        if (!isProcess(context, context.getPackageName())) {
            Timber.tag("ServiceMgr").e("is not main Process");
            return;
        }
        Timber.tag("ServiceMgr").e("initSelf1");


        ServiceMgr.connect(() -> LogMgr.e("ServiceMgr", "onConnected"));

        IPlayController instance = PlayControllerImpl.getInstance();
        if (instance instanceof PlayControllerImpl) {
            ((PlayControllerImpl) instance).init();
        }
        TempPlayListManager.getInstance().init();

    }

    @Override
    public Pair<String, Object> getServicePair() {
        return new Pair<>(IMediaService.class.getName(), new MediaServiceImpl());
    }

    public static Context getContext() {
        return app;
    }

    public static boolean isProcess(Context context, String process){
        if(TextUtils.isEmpty(process)){
            return false;
        }
        return process.equals(ApplicationUtils.getCurrentProcessName());
    }

}
