package com.tme.upgrade.ui.normal;

import com.tme.upgrade.entity.UpgradeInfo;
import com.tme.upgrade.mgr.UpgradeMgr;

public class UpgradeModel {

    public void fetchConfig(OnFetchCallback onFetchCallback) {
        onFetchCallback.onFetch(UpgradeMgr.getInstance().getUpgradeInfo());
    }

    public interface OnFetchCallback {
        void onFetch(UpgradeInfo upgradeInfo);
    }
}
