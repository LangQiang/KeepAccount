package com.lazylite.media.remote.core.down;


import com.lazylite.mod.receiver.network.NetworkStateUtil;

// by haiping 恶心的需求配危险的实现
public final class BitrateInfo {
	private BitrateInfo() {
	}

	public static String getBitrateString(final DownloadProxy.Quality quality, final DownloadProxy.DownType type) {
		return bitratesStrings[getBitrateIdx(quality)][type == DownloadProxy.DownType.SONG||type==DownloadProxy.DownType.WIFIDOWN ||type==DownloadProxy.DownType.OFFLINE? 1 : 0];
	}

	public static int getBitrateNum(final DownloadProxy.Quality quality, final DownloadProxy.DownType type) {
		return bitrateNum[getBitrateIdx(quality)][type == DownloadProxy.DownType.SONG||type==DownloadProxy.DownType.WIFIDOWN||type==DownloadProxy.DownType.OFFLINE ? 1 : 0];
	}

	private static int getBitrateIdx(final DownloadProxy.Quality quality) {
		int idx = quality.ordinal();
		if (quality == DownloadProxy.Quality.Q_AUTO) {
			String network = NetworkStateUtil.getNetworkTypeName();
			if (network.equals("2G")) {
				idx = 1;
			} else if (network.equals("WIFI")) {
				idx = 2;
			}
		}
		return idx;
	}

	// 恶心的逻辑
	private static String[][]	bitratesStrings	= new String[][]
												{ // 试听，下载
												{ "128kmp3", "128kmp3" },	//AUTO3G
												{ "48kaac", "48kaac" },		//LOW/AUTO2G
												{ "128kmp3", "128kmp3" },	//HIGH/AUTOWIFI
												{ "320kmp3", "320kmp3" },			//PERFECT
												{ "2000kflac", "2000kflac" },		//LOSSLESS
												{ "MP4L", "MP4L" },
												{ "MP4", "MP4" },
												{ "MP4HV", "MP4HV" },
												{ "MP4UL", "MP4UL" },
												{ "MP4BD", "MP4BD" }};

	private static int[][]		bitrateNum		= new int[][]
												{
												{ 128, 128 },
												{ 48, 48 },
												{ 128, 128 },
												{ 320, 320 },
												{ 2000, 2000 },
												{ 3000, 3000 },
												{ 4000, 4000 },
												{ 5000, 5000 },
												{ 6000, 6000 },
												{ 7000, 7000 }
												};
}
