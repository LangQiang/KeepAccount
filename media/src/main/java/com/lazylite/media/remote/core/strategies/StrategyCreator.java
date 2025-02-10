package com.lazylite.media.remote.core.strategies;

import com.lazylite.media.remote.core.down.DownloadProxy;

public class StrategyCreator {

	public static final IStrategy createStrategy(DownloadProxy.DownType type) {
		switch (type) {
			case TINGSHU:
			case TSPREFETCH:
				return new TingShuPlayStrategy();
			default:
				break;
		}
		return null;
	}
}
