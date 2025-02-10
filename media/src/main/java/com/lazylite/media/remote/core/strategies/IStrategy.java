package com.lazylite.media.remote.core.strategies;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.remote.core.down.DownloadTask;

public interface IStrategy {
	
	String makeUrl(final AudioInfo audioInfo);
	
	String createTempPath(final DownloadTask task);
	
	String createSavePath(final DownloadTask task);
	
	boolean onSuccess(final DownloadTask task);

}
