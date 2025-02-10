package com.lazylite.media.remote.core.ijkwrapper;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.bean.PlayLogInfo;
import com.lazylite.media.playctrl.PlayProxy;
import com.lazylite.media.remote.AIDLPlayDelegate;


// by haiping
public interface IPlayCtrl {
	void setDelegate(final AIDLPlayDelegate delegate);
    PlayDelegate.ErrorCode playLocal(final String filePath, final boolean isRadio, final PlayDelegate.PlayContent playContent, final int continuePos);
	PlayDelegate.ErrorCode playLocal(final AudioInfo audioInfo, final int continuePos);
	PlayDelegate.ErrorCode playNet(final AudioInfo audioInfo, final int continuePos);

	void pause();

	void resume();

	void stop();

	PlayProxy.Status getStatus();

	int getDuration();

	int getCurrentPos();

	int getBufferPos();

	int getPreparingPercent();

	void seek(final int pos);
	
	boolean getPlayLogInfo(PlayLogInfo info);

	void setEndTime(int endTime);

	void setSpeed(float speed);
	float getSpeed();
}
