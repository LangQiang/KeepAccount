package com.lazylite.play;


import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qyh
 * @date 2022/3/15
 * describe:
 */
public class PlayHelper {
    public static PlayHelper get() {
        return Holder.playHelper;
    }


    private static class Holder {
        private static final PlayHelper playHelper = new PlayHelper();
    }

    public AudioTrack getLogAudioTrack(int encoding, int channelMask, int sampleRate, int bufferSizeInBytes, int mode, int sessionId, AudioAttributes audioAttributes) {
        AudioTrack dtAudioTrack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && audioAttributes != null) {
            AudioFormat audioFormat = new AudioFormat.Builder().setEncoding(encoding).setChannelMask(channelMask).setSampleRate(sampleRate).build();
            dtAudioTrack = new AudioTrack(audioAttributes, audioFormat, bufferSizeInBytes, mode, sessionId);
        } else {
            dtAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelMask, encoding, bufferSizeInBytes, mode, sessionId);
        }
        return dtAudioTrack;
    }
    public void bindLogPlayInfo(AudioTrack audioTrack) {
    }
}
