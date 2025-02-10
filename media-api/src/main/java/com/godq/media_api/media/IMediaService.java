package com.godq.media_api.media;

import androidx.annotation.NonNull;

public interface IMediaService {

    @NonNull
    IPlayController getPlayController();

}
