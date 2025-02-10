package com.lazylite.play.playpage.widget;

import android.util.Log;

import com.godq.media_api.media.IPlayController;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.mod.utils.KwTimer;
import com.lazylite.play.playpage.widget.newseekbar.PlaySeekBar;
import com.lazylite.play.playpage.widget.seekbar.LifeclycleHelper;
import com.lazylite.play.playpage.widget.seekbar.TimeUtils;

import java.util.Locale;

/**
 * @author qyh
 * @date 2022/1/10
 * describe:
 */
public class SeekBarDelegate extends LifeclycleHelper implements KwTimer.Listener {
    private static final int TIMER_INTERVAL = 1000;
    private KwTimer mKwTimer;
    private OnSeekListener mListener;
    private boolean isDragingSeekBar;
    private IPlayController playController;

    public interface OnSeekListener {

        void onPlaySeekBarChangeByTimer(String currentTime, String duration, int progress, int cacheProgress);

        void onPlaySeekBarChangeByUser(String currentTime, String duration);
    }

    public SeekBarDelegate() {
        mKwTimer = new KwTimer(this);
    }

    public void attach(PlaySeekBar seekBar, OnSeekListener onSeekListener) {
        mListener = onSeekListener;
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener());
        refreshSeekBarProgress();
        playController = PlayControllerImpl.getInstance();

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSeekBarProgress();
        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onTimer(KwTimer timer) {
        refreshSeekBarProgress();
    }

    public void startTimer() {
        if (mKwTimer != null && !mKwTimer.isRunnig()) {
            mKwTimer.start(TIMER_INTERVAL);
            refreshSeekBarProgress();
        }
    }

    public void stopTimer() {
        if (mKwTimer != null && mKwTimer.isRunnig()) {
            mKwTimer.stop();
        }
    }

    public class OnSeekBarChangeListener implements PlaySeekBar.OnSeekChangeListener {

        @Override
        public void onProgressChanged(int progress, boolean touch) {
            if(touch) {
                setDurationWhenChanged(progress);
            }
        }

        @Override
        public void onStartTrackingTouch() {
            isDragingSeekBar = true;
        }

        @Override
        public void onStopTrackingTouch(int progress) {
            isDragingSeekBar = false;
        }
    }

    private void refreshSeekBarProgress() {
        if (isDragingSeekBar || mListener == null || playController == null) {
            return;
        }
        int durTime = playController.getDuration();
        int curTime = playController.getCurrentProgress();

        String curTimeFormatStr = TimeUtils.formatTime(curTime);
        String durTimeFormatStr = TimeUtils.formatTime(durTime);
        int progress = 0;
        int cacheProgress = 0;
        if (durTime != 0) {
            int cachePos = playController.getCachePos();
            progress = (int) (curTime * 1.0 / durTime * 100);
            cacheProgress = (int) (cachePos * 1.0 / durTime * 100);
        }

//        Log.e("qyh", "refreshSeekBarProgress progress:===== " + progress + ";;;;cacheProgress=====" + cacheProgress);
        mListener.onPlaySeekBarChangeByTimer(curTimeFormatStr, durTimeFormatStr, progress, cacheProgress);
    }

    private void setDurationWhenChanged(int progress) {
        if (mListener == null || playController == null) {
            return;
        }

        int durTime = playController.getDuration();
        int curTime = (int) (progress * 1.0 / 100 * durTime);
        int curmin, cursec, durmin, dursec;
        cursec = (curTime / 1000) % 60;
        curmin = curTime / 60000;
        dursec = (durTime / 1000) % 60;
        durmin = durTime / 60000;

//        String curTimeFormatStr = String.format(Locale.getDefault(), "%02d:%02d", curmin, cursec);
//        String durTimeFormatStr = String.format(Locale.getDefault(), "%02d:%02d", durmin, dursec);
        String curTimeFormatStr = TimeUtils.formatTime(curTime);
        String durTimeFormatStr = TimeUtils.formatTime(durTime);
        mListener.onPlaySeekBarChangeByUser(curTimeFormatStr, durTimeFormatStr);
        playController.seekTo(curTime);
    }
}
