package com.lazylite.media.remote.core.down;

import com.lazylite.mod.messagemgr.ThreadMessageHandler;

// by haiping
public final class DownloadProxy {
	
	public enum DownGroup { // 同group的任务同一时刻只有一个进行，根据DownType优先级决定，不同的任务group并行执行
		MUSIC,
		GAME,
		APP,
		SKIN,
		BURN,
		HTML,
		TINGSHU
		// 需要的话再添加其它类型
	}
	
	public enum DownType {
		MIN,
		OFFLINE,
		WIFIDOWN,
		TSPREFETCH,//听书资源预取
		PREFETCH,
		DOWNMV,
		SONG, //歌曲手动下载
		FILE, //文件（apk，皮肤）下载
		BURN,  // 煲机
		PLAY, //歌曲在线播放
		RADIO, // 一个非常不好的地方，优先级里面的奇葩，跟DownGroup一样的无奈
		TINGSHU,//听书播放
		MAX
	}

	public enum Quality {
		Q_AUTO,
		Q_LOW,
		Q_HIGH,
		Q_PERFECT,
		Q_LOSSLESS,
		Q_MV_LOW,
		Q_MV_HIGH,
		Q_MV_HD,
		Q_MV_SD,
		Q_MV_BD;
		
		public static Quality bitrate2Quality(final int bitrate) {
			if(bitrate == 0){
				return Q_AUTO;
			} else if(bitrate <= 48) {
				return Q_LOW;
			} else if (bitrate <= 128) {
				return Q_HIGH;
			} else if (bitrate <= 320) {
				return Q_PERFECT;
			} else {
				return Q_LOSSLESS;
			}
		}
	}


	////////
	protected DownloadProxy(final ThreadMessageHandler handler) {
		this.threadHandler = handler;
	}

	private ThreadMessageHandler	threadHandler;
}
