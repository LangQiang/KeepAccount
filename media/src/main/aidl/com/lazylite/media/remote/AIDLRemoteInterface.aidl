package com.lazylite.media.remote;

import com.lazylite.media.remote.AIDLPlayDelegate;
import com.lazylite.media.bean.PlayLogInfo;
import com.lazylite.media.bean.AudioInfo;

interface AIDLRemoteInterface {

	void onConnect();

	void killYourself();

	void setDelegate(AIDLPlayDelegate delegate);

	void play(in AudioInfo audioInfo, int continuePos);

    void prefetch(in AudioInfo audioInfo);

	void cancelPrefetch();

	void stop();

	void cancel();

	void clearPlayList();

	void pause();

	void resume();

	void seek(int pos);

	int getStatus();

	int getDuration();

	int getCurrentPos();

	int getBufferPos();

	int getPreparingPercent();

	int getMaxVolume();

	int getVolume();

	void setVolume(int volume);

	boolean isMute();

	void setMute(boolean mute);

	void setNoRecoverPause();

	void updateVolume();

	PlayLogInfo getPlayLogInfo();

	void setSpectrumEnable(boolean enable);

	void setSpeed(float speed);

    float getSpeed();

    void clearCache(in AudioInfo audioInfo);
}