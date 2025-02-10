package com.lazylite.media.utils;

import android.text.TextUtils;

import com.lazylite.media.Media;

import java.io.File;

public class MediaDirs {

    private static String innerRootPath = "";

    public static final int TEMP = 1;
    public static final int PLAY_CACHE = 2;
    public static final int IJK_LOG = 3;

    public static String getMediaPath(int type) {

        String suffixDir;

        switch (type) {
            case TEMP:
                suffixDir = ".temp";
                break;
            case PLAY_CACHE:
                suffixDir = ".playcache";
                break;
            case IJK_LOG:
                suffixDir = "log";
                break;
            default:
                suffixDir = "unknown";

        }

        String dirPath = getInnerRootPath() + suffixDir + File.separator;

        checkDirPath(dirPath);

        return dirPath;
    }

    private static void checkDirPath(String dirPath) {
        if (!TextUtils.isEmpty(dirPath) && !dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static String getInnerRootPath() {
        if (TextUtils.isEmpty(innerRootPath)) {
            File file = Media.getContext().getExternalFilesDir(null);
            if (file == null) {
                file = Media.getContext().getFilesDir();
            }
            if (file == null) { //仍然为null
                return "";
            }
            innerRootPath = file.getAbsolutePath() + File.separator + "WuLiJinZhu" + File.separator;
        }
        return innerRootPath;
    }

}
