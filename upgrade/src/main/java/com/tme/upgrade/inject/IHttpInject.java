package com.tme.upgrade.inject;

public interface IHttpInject {
    void get(String url, OnRequestCallBack onRequestCallBack);
    IHttpWrapper asyncDownload(String url, String savePath, DownloadCallback downloadCallback);

    interface OnRequestCallBack {
        void callback(String result);
    }

    interface DownloadCallback {

        default void onComplete(){}

        default void onError(int errorCode, String msg){}

        default void onStart(long startPos, long totalLength){}

        default void onProgress(long currentPos, long totalLength){}

        default void onCancel(){}
    }

    interface IHttpWrapper {
        void cancel();
    }
}
