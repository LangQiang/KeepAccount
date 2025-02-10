package com.godq.media_api.media;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        PlayStatus.STATUS_INIT,
        PlayStatus.STATUS_PLAYING,
        PlayStatus.STATUS_BUFFERING,
        PlayStatus.STATUS_PAUSE,
        PlayStatus.STATUS_STOP
})

@Retention(RetentionPolicy.SOURCE)
public @interface PlayStatus {
    int STATUS_INIT = 0;
    int STATUS_PLAYING = 1;
    int STATUS_BUFFERING = 2;
    int STATUS_PAUSE = 3;
    int STATUS_STOP = 4;
}
