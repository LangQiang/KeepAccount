package com.lazylite.media;

import androidx.annotation.NonNull;

import com.godq.media_api.media.IMediaService;
import com.godq.media_api.media.IPlayController;
import com.lazylite.media.playctrl.PlayControllerImpl;

public class MediaServiceImpl implements IMediaService {

    @NonNull
    @Override
    public IPlayController getPlayController() {
        return PlayControllerImpl.getInstance();
    }


}
