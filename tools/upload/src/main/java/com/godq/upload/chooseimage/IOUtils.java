package com.godq.upload.chooseimage;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by lzf on 2022/1/6 10:37 上午
 */
public class IOUtils {
    public static void closeIoStream(Closeable closeable) {
        if (closeable != null && closeable instanceof Closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                // silence
            }
        }
    }

}
