package com.tme.upgrade.mgr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tme.upgrade.Utils;
import com.tme.upgrade.entity.UpgradeInfo;
import com.tme.upgrade.inject.IHttpInject;
import com.tme.upgrade.inject.InfoInject;
import com.tme.upgrade.strategy.ForceUpgradeStrategy;
import com.tme.upgrade.strategy.IStrategy;
import com.tme.upgrade.strategy.NormalUpgradeStrategy;

import java.io.File;
import java.lang.ref.WeakReference;

import timber.log.Timber;

public class UpgradeMgr implements IUpgrade {

    private WeakReference<Activity> activityWeakReference;

    private UpgradeInfo upgradeInfo;

    private IHttpInject iHttpInject;

    private InfoInject infoInject;

    private String finalPath;

    private IStrategy iStrategy;
    private IHttpInject.IHttpWrapper iHttpWrapper;
    private boolean isStartDownload;

    private UpgradeMgr() {

    }

    public static IUpgrade getInstance() {
        return Inner.INSTANCE;
    }

    private static class Inner {
        private static final UpgradeMgr INSTANCE = new UpgradeMgr();
    }

    @Override
    public void init(IHttpInject iHttpInject, InfoInject infoInject) {
        this.iHttpInject = iHttpInject;
        this.infoInject = infoInject;
    }

    @Override
    public void loadConfig(Activity activity) {
        if (isInjectInvalid() || activity == null) {
            return;
        }
        if (activityWeakReference == null || activityWeakReference.get() == null) {
            activityWeakReference = new WeakReference<>(activity);
        }
        iHttpInject.get(infoInject.getUpdateUrl(), this::onHttpCallback);
    }


    @Override
    public void downloadApk(OnDownloadListener onDownloadListener) {
        if (upgradeInfo == null || isInjectInvalid() || isStartDownload) {
            return;
        }

        File downloadFile = new File(infoInject.getSaveRootPath(), upgradeInfo.newVersion + ".apk.temp");
        File finalFile = new File(infoInject.getSaveRootPath(), upgradeInfo.newVersion + ".apk");
        finalPath = finalFile.getAbsolutePath();

        if (finalFile.exists()) {
            if (onDownloadListener != null) {
                onDownloadListener.onComplete();
            }
            downloadComplete();
            return;
        }

        if (downloadFile.exists()) {
            if (!downloadFile.delete()) {
                debugLog("文件删除失败");
            }
        }
        String downloadPath = downloadFile.getAbsolutePath();
        isStartDownload = true;
        iHttpWrapper = iHttpInject.asyncDownload(upgradeInfo.downUrl, downloadPath, new IHttpInject.DownloadCallback() {
            @Override
            public void onComplete() {
                if (Utils.renameFile(downloadPath, finalPath)) {
                    if (onDownloadListener != null) {
                        onDownloadListener.onComplete();
                    }
                    downloadComplete();
                }
                iHttpWrapper = null;
            }

            @Override
            public void onError(int errorCode, String msg) {
                debugLog("errorCode:" + errorCode + "  msg:" + msg);
                if (onDownloadListener != null) {
                    onDownloadListener.onError(errorCode, msg);
                }
                iHttpWrapper = null;
                isStartDownload = false;
            }

            @Override
            public void onStart(long startPos, long totalLength) {
                if (onDownloadListener != null) {
                    onDownloadListener.onStart(startPos, totalLength);
                }
            }

            @Override
            public void onProgress(long currentPos, long totalLength) {
                if (onDownloadListener != null) {
                    onDownloadListener.onProgress(currentPos, totalLength);
                }
                debugLog("current:" + currentPos + "   totalLength:" + totalLength);
            }

            @Override
            public void onCancel() {
                if (onDownloadListener != null) {
                    onDownloadListener.onCancel();
                }
                iHttpWrapper = null;
                isStartDownload = false;
            }
        });

    }

    @Override
    public void cancelDownload() {
        if (iHttpWrapper != null) {
            iHttpWrapper.cancel();
        }
    }

    private void downloadComplete() {
        if (iStrategy != null) {
            iStrategy.onDownloadFinish();
        }
    }

    @Override
    public void installApk() {
        if (!isActivityAlive()) {
            return;
        }
        try {
            if (TextUtils.isEmpty(finalPath)) {
                debugLog("bad params");
                return;
            }
            debugLog("url = " + finalPath);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);


            if (finalPath.startsWith("file:")) {
                i.setDataAndType(Uri.parse(finalPath), "application/vnd.android.package-archive");
            } else {
                i.setDataAndType(Utils.getUriForFile(activityWeakReference.get(), new File(finalPath)), "application/vnd.android.package-archive");
            }

            activityWeakReference.get().startActivity(i);
            debugLog("run apk finished");
        } catch (Exception e) {
            debugLog("Exception:" + e.getMessage());
        }
    }

    @Override
    public UpgradeInfo getUpgradeInfo() {
        return upgradeInfo;
    }

    private void onHttpCallback(String result) {
        debugLog(result);
        upgradeInfo = Utils.parse(result);
        if(upgradeInfo == null) return;
        debugLog(upgradeInfo.toString());
        iStrategy = upgradeInfo.force ? new ForceUpgradeStrategy(activityWeakReference, infoInject) : new NormalUpgradeStrategy(activityWeakReference, infoInject);
        if (needUpgrade()) {
            iStrategy.onLoadConfigFinish();
        }
    }

    private boolean needUpgrade() {
        return !isInjectInvalid()
                && upgradeInfo != null
                && upgradeInfo.upgrade
                && !TextUtils.isEmpty(upgradeInfo.newVersion)
                && Utils.compareVersion(upgradeInfo.newVersion, infoInject.getAppVersion()) > 0;
    }



    private boolean isInjectInvalid() {
        return infoInject == null || iHttpInject == null;
    }

    private boolean isActivityAlive() {
        return activityWeakReference != null && activityWeakReference.get() != null;
    }

    private void debugLog(String msg) {
        if (isInjectInvalid() || !infoInject.isDebug() || msg == null) {
            return;
        }
        Timber.tag("AppUpgrade").e(msg);
    }
}
