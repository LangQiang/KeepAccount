package com.tme.upgrade.entity;

import androidx.annotation.NonNull;

public class UpgradeInfo {
    public String title;
    public String tip;
    public String cancelTip;
    public String confirmTip;

    public boolean upgrade;
    public boolean force;
    public String newVersion;
    public String downUrl;

    public boolean isDownloadOnBackground = false;

    @NonNull
    @Override
    public String toString() {
        return "UpgradeInfo{" +
                "title='" + title + '\'' +
                ", tip='" + tip + '\'' +
                ", cancelTip='" + cancelTip + '\'' +
                ", confirmTip='" + confirmTip + '\'' +
                ", upgrade=" + upgrade +
                ", force=" + force +
                ", newVersion='" + newVersion + '\'' +
                ", downUrl='" + downUrl + '\'' +
                '}';
    }
}
