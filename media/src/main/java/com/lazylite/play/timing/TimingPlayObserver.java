package com.lazylite.play.timing;

import com.godq.media_api.media.IPlayObserver;

public class TimingPlayObserver implements IPlayObserver {

    TimingPlayObserver() {
    }

    @Override
    public void onPlay() {
        TsSleepCtr.getIns().onPlayNewOne();
    }

    @Override
    public void onPreStart(boolean isLocal) {

    }

    @Override
    public void onRealPlay() {

    }

    @Override
    public void cacheProgress(int currentProgress, int total) {

    }

    @Override
    public void cacheFinished(String savePath, long id) {

    }

    @Override
    public void playProgress(int playPos, int total) {

    }

    @Override
    public void seekSuccess(int pos) {

    }

    @Override
    public void onStop(boolean end) {

    }

    @Override
    public void onFailed(int errCode, String msg) {

    }

    @Override
    public void onContinue() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void FFTDataReceive(float[] leftFFT, float[] rightFFT) {

    }

    @Override
    public void volumeChanged(int vol) {

    }

    @Override
    public void muteChanged(boolean mute) {

    }

    @Override
    public void speedChanged(float mCurSpeed) {

    }

    @Override
    public void playModeChanged(int mCurPlayMode) {

    }
}
