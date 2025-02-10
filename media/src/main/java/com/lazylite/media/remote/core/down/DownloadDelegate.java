package com.lazylite.media.remote.core.down;


import com.lazylite.media.remote.AIDLDownloadDelegate;

//by haiping
public abstract class DownloadDelegate extends AIDLDownloadDelegate.Stub {
	

	public enum ErrorCode {
		SUCCESS,
		ANTISTEALING_FAILED, // 防盗链失败
		NO_NET,
		NET_ERROR,
		NET_CDN_ERROR,
		IO_ERROR,
		NO_SDCARD,
		NOSPACE,
		ONLYWIFI,
		NO_AUTH,//检查权限没有通过
		NO_AUTH_NEED_OPEN_VIP,//检查权限没有通过,并通知,需要开通vip
		NO_AUTH_COST_NOT_ENOUGH_UPGRADE_VIP,//检查权限没有通过,并通知,点数不够,需要升级
		NO_AUTH_NEED_RENEW_VIP_OUTTIME,//检查权限没有通过,并通知,点数不够,vip过期,需要续费
		NO_AUTH_NEED_RENEW_VIP_NEXT,//检查权限没有通过,并通知,点数不够,,需要续费，下个月下载
		KSING_ONLYWIFI,
		OTHERS // OMG这是sha？
	}
	
	public enum DataSrc {
		NET,
		LOCAL_PART,
		LOCAL_FULL
	}

	@Override
	public final void DownloadDelegate_Start(int id, String url, String tempPath, int totalLen, int currentLen,
                                             int bitrate, int dataSrc) {
		DownloadDelegate_Start(id, url, tempPath, totalLen, currentLen, bitrate, DataSrc.values()[dataSrc]);
	}

	@Override
	public final void DownloadDelegate_Finish(int id, int err, String savePath) {
		DownloadDelegate_Finish(id, ErrorCode.values()[err], savePath);
	}

	// 已缓存资源httpInfo=null
	public abstract void DownloadDelegate_Start(final int id, final String url, final String tempPath,
                                                final int totalLen, final int currentLen, final int bitrate, final DataSrc dataSrc);

	// speed:k/s
	public abstract void DownloadDelegate_Progress(final int id, final int totalLen, final int current,
			final float speed);

	// remove的任务不会收到这个通知
	public abstract void DownloadDelegate_Finish(final int id, final DownloadDelegate.ErrorCode err, final String savePath);
}
