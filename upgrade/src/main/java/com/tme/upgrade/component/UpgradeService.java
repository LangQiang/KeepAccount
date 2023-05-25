package com.tme.upgrade.component;

import android.app.Activity;

import com.tme.upgrade.mgr.UpgradeMgr;

import cn.tme.upgrade_api.IUpgradeService;

public class UpgradeService implements IUpgradeService {
    @Override
    public void check(Activity activity) {
        if (activity == null) return;
        UpgradeMgr.getInstance().loadConfig(activity);
    }
}
