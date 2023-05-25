package com.tme.upgrade.mgr;

import android.app.Activity;

import com.tme.upgrade.entity.UpgradeInfo;
import com.tme.upgrade.inject.IHttpInject;
import com.tme.upgrade.inject.InfoInject;

public interface IUpgrade {

    void init(IHttpInject iHttpInject, InfoInject infoInject);

    void loadConfig(Activity activity);

    void downloadApk(OnDownloadListener onDownloadListener);

    void cancelDownload();

    void installApk();

    UpgradeInfo getUpgradeInfo();

    interface OnDownloadListener {

        default void onComplete(){}

        default void onError(int errorCode, String msg){}

        default void onStart(long startPos, long totalLength){}

        default void onProgress(long currentPos, long totalLength){}

        default void onCancel(){}
    }
}
