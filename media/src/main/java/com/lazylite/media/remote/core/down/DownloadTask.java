package com.lazylite.media.remote.core.down;

import android.os.Handler;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.remote.core.play.AntiStealing;
import com.lazylite.media.remote.core.strategies.IStrategy;
import com.lazylite.media.remote.AIDLDownloadDelegate;

import cn.kuwo.p2p.Sign;

//by haiping
public final class DownloadTask {
	public int								taskID;
	public boolean							started;		// 启动，可能在下载，也可能因为优先级低被挂起
	public boolean							running;		// 被调度，正在下载
	public AIDLDownloadDelegate delegate;
	public DownloadProxy.DownType			type;
	public DownloadProxy.Quality			quality;
	public String url;
	public String format;
	public int								bitrate;
	public String savePath;		// 下载文件的目标路径，下载、预取、缓存歌曲的话留空，如果是下载歌曲，Mgr会计算一个保存路径
	public Handler targetHandler;
	public AntiStealing.AntiStealingResult	antiResult;
	public String tempPath;
	public IStrategy downloadStrategy;
	public Sign sign; //CD包只支持p2p下载
	public AudioInfo audioInfo;
}