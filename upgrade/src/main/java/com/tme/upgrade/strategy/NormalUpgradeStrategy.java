package com.tme.upgrade.strategy;

import android.app.Activity;

import com.tme.upgrade.inject.InfoInject;
import com.tme.upgrade.mgr.UpgradeMgr;
import com.tme.upgrade.ui.normal.NormalUpgradeDialog;

import java.lang.ref.WeakReference;

public class NormalUpgradeStrategy implements IStrategy {

    private final WeakReference<Activity> weakReference;

    private final InfoInject infoInject;

    public NormalUpgradeStrategy(WeakReference<Activity> weakReference, InfoInject infoInject) {
        this.weakReference = weakReference;
        this.infoInject = infoInject;
    }

    @Override
    public void onLoadConfigFinish() {
        if (weakReference == null) {
            return;
        }
        Activity activity = weakReference.get();
        if (activity == null || activity.isFinishing() /*|| !infoInject.isForeground()*/) {
            return;
        }
        new NormalUpgradeDialog(activity).show();
    }

    @Override
    public void onDownloadFinish() {
        if (UpgradeMgr.getInstance().getUpgradeInfo().isDownloadOnBackground) {
            UpgradeMgr.getInstance().installApk();
        }
    }
}
