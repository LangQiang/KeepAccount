package com.tme.upgrade.component;

import android.content.Context;
import android.util.Pair;

import com.lazylite.annotationlib.AutoInit;
import com.lazylite.bridge.init.Init;
import com.lazylite.mod.http.mgr.HttpWrapper;
import com.lazylite.mod.http.mgr.IKwHttpFetcher;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.mgr.model.DownReqInfo;
import com.lazylite.mod.http.mgr.model.RequestInfo;
import com.lazylite.mod.http.mgr.test.UrlEntrustUtils;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.utils.AppInfo;
import com.lazylite.mod.utils.KwDirs;
import com.tme.upgrade.inject.IHttpInject;
import com.tme.upgrade.inject.InfoInject;
import com.tme.upgrade.mgr.UpgradeMgr;

import cn.tme.upgrade_api.IUpgradeService;
import timber.log.Timber;

@AutoInit
public class UpgradeInit extends Init {

    private static final String HOST = UrlEntrustUtils.entrustHost("http://43.138.100.114", "http://43.138.100.114");

    @Override
    public void init(Context context) {
        UpgradeMgr.getInstance().init(iHttpInject, infoInject);
    }

    @Override
    public void initAfterAgreeProtocol(Context context) {

    }

    @Override
    public Pair<String, Object> getServicePair() {
        return new Pair<>(IUpgradeService.class.getName(), new UpgradeService());
    }

    private final IHttpInject iHttpInject = new IHttpInject() {
        @Override
        public void get(String url, OnRequestCallBack onRequestCallBack) {
            KwHttpMgr.getInstance().getKwHttpFetch().asyncGet(RequestInfo.newGet(url), responseInfo -> {
                String data = responseInfo.dataToString();
                Timber.tag("AppUpgrade").d("upgrade: %s", data);
                if (onRequestCallBack != null) {
                    onRequestCallBack.callback(data);
                }
            });
        }

        @Override
        public IHttpWrapper asyncDownload(String url, String savePath, DownloadCallback downloadCallback) {
            DownReqInfo downReqInfo = new DownReqInfo(url, savePath, 0);
            HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper = KwHttpMgr.getInstance().getKwHttpFetch().asyncDownload(downReqInfo, null, new IKwHttpFetcher.DownloadListener() {
                @Override
                public void onComplete(HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
                    if (downloadCallback != null) {
                        downloadCallback.onComplete();
                    }
                }

                @Override
                public void onError(int errorCode, String msg, HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
                    if (downloadCallback != null) {
                        downloadCallback.onError(errorCode, msg);
                    }
                    Timber.tag("AppUpgrade").d("onProgress: %s", msg);
                }

                @Override
                public void onStart(long startPos, long totalLength, HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
                    if (downloadCallback != null) {
                        downloadCallback.onStart(startPos, totalLength);
                    }
                }

                @Override
                public void onProgress(long currentPos, long totalLength, HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
                    if (downloadCallback != null) {
                        downloadCallback.onProgress(currentPos, totalLength);
                    }
                    Timber.tag("AppUpgrade").d("onProgress: %s", currentPos);
                }

                @Override
                public void onCancel(HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
                    if (downloadCallback != null) {
                        downloadCallback.onCancel();
                    }
                }
            });
            return httpWrapper::cancel;
        }
    };

    private final InfoInject infoInject = new InfoInject() {

        @Override
        public String getAppVersion() {
            return AppInfo.VERSION_CODE;
        }

        @Override
        public boolean isDebug() {
            return AppInfo.IS_DEBUG;
        }

        @Override
        public boolean isWiFi() {
            return NetworkStateUtil.isWifi();
        }

        @Override
        public boolean isForeground() {
            return AppInfo.IS_FORGROUND;
        }

        @Override
        public String getSaveRootPath() {
            return KwDirs.getDir(KwDirs.UPDATE);
        }

        @Override
        public String getUpdateUrl() {
            return HOST + "/config/upgrade/checkUpgrade";
        }
    };
}
