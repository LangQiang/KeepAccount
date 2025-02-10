package com.lazylite.media.remote.core.strategies;

import android.text.TextUtils;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.remote.core.down.DownCacheMgr;
import com.lazylite.media.remote.core.down.DownloadTask;
import com.lazylite.media.utils.MediaDirs;
import com.lazylite.mod.http.mgr.test.UrlEntrustUtils;

public class TingShuPlayStrategy implements IStrategy {

	private final static String host = UrlEntrustUtils.entrustHost("https://mp.tencentmusic.com", "https://mp.tencentmusic.com");

	// 防盗链URL
	// rid: 歌曲的rid, 如，34564564
	// rate:需要的资源类型和码率，大小写不区分，如，128kmp3|48kaac|192kmp3|320kmp3
	// format:客户端支持的音频文件格式，若服务器上存在多种格式的音频资源，则按先后顺序返回第一种，如，mp3|aac
	@Override
	public String makeUrl(AudioInfo audioInfo) {
	    return host + "/api/v1/fmac/track/play?"
                + "trackId=" + audioInfo.rid + "&trackMd5=" + audioInfo.musicMd5;
	}

	@Override
	public String createTempPath(final DownloadTask task) {
		StringBuilder builder = new StringBuilder();
		builder.append(MediaDirs.getMediaPath(MediaDirs.PLAY_CACHE));
		builder.append(DownCacheMgr.TING_SHU_CACHE_EXT);
		builder.append(".");
		builder.append(task.audioInfo.albumId);
		builder.append(".");
		builder.append(task.audioInfo.rid).append("-").append(task.audioInfo.musicMd5);
		builder.append(".");
		if (TextUtils.isEmpty(task.format)) {
			builder.append("aac");
		} else {
			builder.append(task.format);
		}
		builder.append(".");
		builder.append(DownCacheMgr.UNFINISHED_CACHE_EXT);
		return builder.toString();
	}

	@Override
	public String createSavePath(final DownloadTask task) {
		if (DownCacheMgr.isUnFinishedCacheSong(task.tempPath)) {
			StringBuilder builder = new StringBuilder();
			builder.append(MediaDirs.getMediaPath(MediaDirs.PLAY_CACHE));
			builder.append(DownCacheMgr.TING_SHU_CACHE_EXT);
			builder.append(".");
			builder.append(task.audioInfo.albumId);
			builder.append(".");
			builder.append(task.audioInfo.rid).append("-").append(task.audioInfo.musicMd5);
			builder.append(".");
			if (TextUtils.isEmpty(task.format)) {
				builder.append("aac");
			} else {
				builder.append(task.format);
			}
			builder.append(".");
			builder.append(DownCacheMgr.FINISHED_CACHE_AUDIO_EXT);
			return builder.toString();
		} else {
			return task.tempPath;
		}

	}

	@Override
	public boolean onSuccess(final DownloadTask task) {
		return DownCacheMgr.moveTempFile2SavePath(task.tempPath, task.savePath);
	}



}
