package com.tme.upgrade;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.tme.upgrade.entity.UpgradeInfo;

import org.json.JSONObject;

import java.io.File;

public class Utils {

    public static Uri getUriForFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && context != null) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static int compareVersion(String version1, String version2) {
        String[] s1 = version1.split("\\.");
        String[] s2 = version2.split("\\.");

        int n1 = s1.length;
        int n2 = s2.length;
        if (n1 < n2) {
            return -1;
        }
        if (n1 > n2) {
            return 1;
        }
        for (int i = 0; i < n1; ++i) {
            int i1 = 0;
            int i2 = 0;
            try {
                i1 = Integer.parseInt(s1[i]);
            } catch (NumberFormatException ignore) {
            }
            try {
                i2 = Integer.parseInt(s2[i]);
            } catch (NumberFormatException ignore) {
            }
            if (i1 < i2) {
                return -1;
            }
            if (i1 > i2) {
                return 1;
            }
        }

        return 0;
    }

    public static UpgradeInfo parse(String result) {
        if (result == null) {
            return null;
        }

        try {
            JSONObject dataObj = new JSONObject(result).optJSONObject("data");
            if (dataObj == null) {
                return null;
            }
            UpgradeInfo upgradeInfo = new UpgradeInfo();
            upgradeInfo.cancelTip = dataObj.optString("cancelBtn");
            if (TextUtils.isEmpty(upgradeInfo.cancelTip)) {
                upgradeInfo.cancelTip = "取消";
            }
            upgradeInfo.confirmTip = dataObj.optString("confirmBtn");
            if (TextUtils.isEmpty(upgradeInfo.confirmTip)) {
                upgradeInfo.confirmTip = "升级";
            }
            upgradeInfo.title = dataObj.optString("title");
            if (upgradeInfo.title.isEmpty()) {
                upgradeInfo.title = "升级提示";
            }
            upgradeInfo.tip = dataObj.optString("tips");
            if (upgradeInfo.tip.isEmpty()) {
                upgradeInfo.tip = "麻溜的";
            }
            upgradeInfo.upgrade = dataObj.optBoolean("upgrade", true);
            upgradeInfo.force = !(dataObj.optInt("force") == 0);
            upgradeInfo.newVersion = dataObj.optString("upgrade_version");
            upgradeInfo.downUrl = dataObj.optString("upgrade_url");
            return upgradeInfo;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean renameFile(String downloadPath, String finalPath) {
        File finalFile = new File(finalPath);
        if (finalFile.exists()) {
            return true;
        }
        File tempFile = new File(downloadPath);
        if (tempFile.exists()) {
            return tempFile.renameTo(finalFile);
        }
        return false;
    }
}
