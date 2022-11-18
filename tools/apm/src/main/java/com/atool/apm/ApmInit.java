package com.atool.apm;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.atool.apm.dokit.KwDokitHelper;
import com.atool.apm.exception.ExceptionHandler;
import com.godq.deeplink.DeepLinkUtils;
import com.godq.deeplink.DeeplinkResult;
import com.godq.deeplink.intercept.IIntercept;
import com.lazylite.annotationlib.AutoInit;
import com.lazylite.bridge.init.Init;
import com.lazylite.mod.utils.TestShowLog;

@AutoInit
public class ApmInit extends Init {

    @Override
    public void init(Context context) {
        if (context instanceof Application) {
            KwDokitHelper.init((Application) context);
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        }

        DeepLinkUtils.addGlobalIntercept(new IIntercept() {
            @NonNull
            @Override
            public Uri beforeRoute(@NonNull Uri uri) {
                TestShowLog.log(uri.toString());
                return uri;
            }

            @NonNull
            @Override
            public DeeplinkResult afterRoute(@NonNull DeeplinkResult result, @NonNull Uri uri) {
                return result;
            }
        });
    }

    @Override
    public void initAfterAgreeProtocol(Context context) {
//        BuglyHelper.initBugly(context);
    }

    @Override
    public Pair<String, Object> getServicePair() {
//        return new Pair<>(IApmService.class.getName(), new ApmServiceImpl());
        return null;
    }
}
