package com.tme.upgrade;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lazylite.mod.http.mgr.HttpWrapper;
import com.lazylite.mod.http.mgr.IKwHttpFetcher;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.mgr.model.DownReqInfo;
import com.lazylite.mod.http.mgr.model.RequestInfo;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.utils.AppInfo;
import com.lazylite.mod.utils.KwDirs;
import com.tme.upgrade.inject.IHttpInject;
import com.tme.upgrade.inject.InfoInject;
import com.tme.upgrade.mgr.UpgradeMgr;
import com.tme.upgrade.ui.force.ForceUpgradeDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        CommonInit.initOnAppCreate(this.getApplicationContext());
        UpgradeMgr.getInstance().init(new IHttpInject() {
            @Override
            public void get(String url, OnRequestCallBack onRequestCallBack) {
                KwHttpMgr.getInstance().getKwHttpFetch().asyncGet(RequestInfo.newGet(url), responseInfo -> {
                    if (onRequestCallBack != null) {
                        onRequestCallBack.callback(responseInfo.dataToString());
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
        }, new InfoInject() {
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
                return "http://test-taeapi.kuwo.cn/api/v1/config/version/checkUpgrade?appUid=968edeb19d4d277c51e669ad00001a916212&deviceId=71a98a7bfe784e68b24e2ffd2aebf48f&source=kwbooklite_ip_1.0.0.0_WY.ipa&nonceStr=SOFYEG&version=1.0.0.0&loginUid=100391&timestamp=1647590138000";
            }

        });
        UpgradeMgr.getInstance().loadConfig(this);

        MessageManager.getInstance().asyncRun(3000, new MessageManager.Runner() {
            @Override
            public void call() {
                new ForceUpgradeDialog(MainActivity.this, UpgradeMgr.getInstance().getUpgradeInfo()).show();
            }
        });
    }
}