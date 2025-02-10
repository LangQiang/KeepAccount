package com.godq.media_api.media;

import com.lazylite.mod.messagemgr.EventId;
import com.lazylite.mod.messagemgr.IObserverBase;

public interface IPlayObserver extends IObserverBase {

    EventId EVENT_ID = () -> IPlayObserver.class;

    default void onPlay() {}

    default void onPreStart(boolean isLocal) {}

    default void onRealPlay() {}

    default void cacheProgress(int currentProgress, int total) {}

    default void cacheFinished(String savePath, long id) {}

    default void playProgress(int playPos, int total) {}

    default void seekSuccess(int pos) {}

    default void onStop(boolean end) {}

    default void onFailed(int errCode, String msg) {}

    default void onContinue() {}

    default void onPause() {}

    default void FFTDataReceive(float[] leftFFT, float[] rightFFT) {}

    default void volumeChanged(int vol) {}

    default void muteChanged(boolean mute) {}

    default void speedChanged(float mCurSpeed) {}

    default void playModeChanged(int mCurPlayMode) {}

    default void clearPlayList() {}
}
