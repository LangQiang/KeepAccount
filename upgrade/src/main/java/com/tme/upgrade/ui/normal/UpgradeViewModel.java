package com.tme.upgrade.ui.normal;

import androidx.databinding.ObservableField;

import com.lazylite.mod.utils.UIHelper;
import com.tme.upgrade.databinding.UpgradeDialogLayoutBinding;
import com.tme.upgrade.entity.UpgradeInfo;
import com.tme.upgrade.mgr.IUpgrade;
import com.tme.upgrade.mgr.UpgradeMgr;

public class UpgradeViewModel {

    public static final int CLICK_TYPE_DOWNLOAD = 0;
    public static final int CLICK_TYPE_AFTER_DOWN = 1;
    public static final int CLICK_TYPE_DOWN_COMPLETE = 2;

    private final UpgradeDialogLayoutBinding viewDataBinding;

    private final ObservableField<String> title = new ObservableField<>();
    private final ObservableField<String> tip = new ObservableField<>();
    private final ObservableField<String> newVersion = new ObservableField<>();
    private final ObservableField<String> cancelTip = new ObservableField<>();
    private final ObservableField<String> confirmTip = new ObservableField<>();
    private final ObservableField<Integer> clickType = new ObservableField<>();

    private final ObservableField<Integer> progress = new ObservableField<>();

    public UpgradeViewModel(UpgradeDialogLayoutBinding viewDataBinding) {
        this.viewDataBinding = viewDataBinding;
        UpgradeModel upgradeModel = new UpgradeModel();
        upgradeModel.fetchConfig(this::setInfo);
    }

    public ObservableField<String> getTitle() {
        return title;
    }

    public ObservableField<String> getTip() {
        return tip;
    }

    public ObservableField<String> getNewVersion() {
        return newVersion;
    }

    public ObservableField<String> getCancelTip() {
        return cancelTip;
    }

    public ObservableField<String> getConfirmTip() {
        return confirmTip;
    }

    public ObservableField<Integer> getClickType() {
        return clickType;
    }

    public ObservableField<Integer> getProgress() {
        return progress;
    }

    public void onConfirmClick(int clickType) {
        if (clickType == CLICK_TYPE_DOWNLOAD) {
            UpgradeMgr.getInstance().downloadApk(onDownloadListener);
        } else if (clickType == CLICK_TYPE_AFTER_DOWN) {
            UpgradeMgr.getInstance().getUpgradeInfo().isDownloadOnBackground = true;
            dismissDialog();
        } else if (clickType == CLICK_TYPE_DOWN_COMPLETE) {
            UpgradeMgr.getInstance().installApk();
        } else {
            dismissDialog();
        }
    }

    public void onCancelClick(int clickType) {
        dismissDialog();
        if (clickType == CLICK_TYPE_AFTER_DOWN) {
            UpgradeMgr.getInstance().cancelDownload();
        }
    }

    private void setInfo(UpgradeInfo upgradeInfo) {
        title.set(upgradeInfo.title);
        tip.set(upgradeInfo.tip);
        cancelTip.set(upgradeInfo.cancelTip);
        confirmTip.set(upgradeInfo.confirmTip);
        newVersion.set(upgradeInfo.newVersion);
        clickType.set(CLICK_TYPE_DOWNLOAD);
    }

    private final IUpgrade.OnDownloadListener onDownloadListener = new IUpgrade.OnDownloadListener() {
        @Override
        public void onComplete() {
            confirmTip.set("安装");
            cancelTip.set("取消");
            title.set("下载完成");
            clickType.set(CLICK_TYPE_DOWN_COMPLETE);
        }

        @Override
        public void onError(int errorCode, String msg) {
            dismissDialog();
        }

        @Override
        public void onStart(long startPos, long totalLength) {
            confirmTip.set("后台下载");
            cancelTip.set("取消更新");
            title.set("下载中");
            clickType.set(CLICK_TYPE_AFTER_DOWN);
        }

        @Override
        public void onProgress(long pos, long total) {
            progress.set(total == 0 ? 0 : (int) (pos * 1000f / total));
        }

        @Override
        public void onCancel() {
            dismissDialog();
        }
    };

    private void dismissDialog() {
        if (viewDataBinding != null && viewDataBinding.getView() != null) {
            UIHelper.safeDismissDialog(viewDataBinding.getView());
        }
    }
}
