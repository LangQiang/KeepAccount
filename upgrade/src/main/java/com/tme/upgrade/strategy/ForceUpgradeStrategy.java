package com.tme.upgrade.strategy;

import android.app.Activity;

import com.tme.upgrade.inject.InfoInject;
import com.tme.upgrade.mgr.UpgradeMgr;
import com.tme.upgrade.ui.force.ForceUpgradeDialog;

import java.lang.ref.WeakReference;

public class ForceUpgradeStrategy implements IStrategy {

    private final WeakReference<Activity> weakReference;

    private final InfoInject infoInject;

    public ForceUpgradeStrategy(WeakReference<Activity> weakReference, InfoInject infoInject) {
        this.weakReference = weakReference;
        this.infoInject = infoInject;
    }

    @Override
    public void onLoadConfigFinish() {
        if (!infoInject.isWiFi()) {
            return;
        }
        UpgradeMgr.getInstance().downloadApk(null);
    }

    @Override
    public void onDownloadFinish() {
        if (weakReference == null) {
            return;
        }
        Activity activity = weakReference.get();
        if (activity == null || activity.isFinishing() || UpgradeMgr.getInstance().getUpgradeInfo() == null /*|| !infoInject.isForeground()*/) {
            return;
        }
        new ForceUpgradeDialog(activity, UpgradeMgr.getInstance().getUpgradeInfo()).show();
    }
}
