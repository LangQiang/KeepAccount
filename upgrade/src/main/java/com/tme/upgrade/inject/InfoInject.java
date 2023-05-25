package com.tme.upgrade.inject;

public interface InfoInject {
    String getAppVersion();
    boolean isDebug();
    boolean isWiFi();
    boolean isForeground();
    String getSaveRootPath();
    String getUpdateUrl();
}
