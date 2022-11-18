package com.atool.apm.dokit;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.atool.apm.dokit.component.DebugComponent;
import com.atool.apm.dokit.component.HostComponent;
import com.atool.apm.dokit.component.LogcatComponent;
import com.atool.apm.dokit.component.PayDialogComponent;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.AbstractKit;

import java.util.ArrayList;
import java.util.List;

public class KwDokitHelper{

    public static void init(Application app) {
        DoraemonKit.install(app, fetchKuwoKits(), "");
    }

    private static List<AbstractKit> fetchKuwoKits() {
        List<AbstractKit> kits = new ArrayList<>();
        kits.add(new DebugComponent());
        kits.add(new HostComponent());
        kits.add(new LogcatComponent());
        kits.add(new PayDialogComponent());
        return kits;
    }

    private static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

}
