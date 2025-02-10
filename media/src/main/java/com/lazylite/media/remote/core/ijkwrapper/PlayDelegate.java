package com.lazylite.media.remote.core.ijkwrapper;

//by haiping
public interface PlayDelegate {


	public enum PlayContent {
		MUSIC,
		KSING,
		TINGSHU
	}
	public enum ErrorCode {
		SUCCESS,							//0
		NETWORK_ERROR_BEGIN,//保留字段		//1
		NETWORK_ERROR_OOT_START, 			//2
		NETWORK_ERROR_OOT_BUFFER,			//3
		NETWORK_ERROR_ANTISTEALING,			//4
		NETWORK_ERROR_DOWNERR,				//5
		NETWORK_ERROR_CDNERR,				//5
		NETWORK_ERROR_END,//保留字段			//6
		FILENOTEXIST, // 无rid信息的本地歌曲对应的本地文件不存在 //7
		DECODE_FAILE,						//8
		NO_DECODER,							//9
		NO_HTTP_URL,						//10
		NO_SDCARD,							//11
		NO_SPACE, // SD卡满					//12
		IO_ERROR,							//13
		OTHERDOWNERR,						//14
		NO_NETWORK,							//15
		UNKNOWN,							//16
		ONLYWIFI, //仅在wifi下联网，但此时不是wifi，失败	//17
		DOWNWHENPLAY, // 关闭边听边存			//18
		SERVICEREST,						//19
		NOCOPYRIGHT,						//20
		KSING_FETCH_MSG_FAIL,//kging获取播放信息失败	//21
		KSING_PRO_NOT_EXIST,//kging作品不存在	//22
		KSING_ONLYWIFI,						//23
		KSING_USER_CANCEL,					//24
		NOT_VIP_USER, //无网络时普通用户没有权限播加密缓存歌曲	//25
		NOT_VIP_BUY_TONE //听书 播放购买提示音
	}

	// 参数说明是否有缓冲过程（本地歌曲、有缓存、已缓存歌曲不需要缓冲），随着本地-网络尝试失败情况，播放开始前可能有1-2个这样的通知
	void PlayDelegate_PreStart(final boolean buffering);

	void PlayDelegate_RealStart(final long realTime);

	void PlayDelegate_Pause();

	void PlayDelegate_Continue();

	// error之后直接停止，不会再发stop消息
	void PlayDelegate_Failed(ErrorCode error);

	// 主动调用播放模块的stop，end为false，正常播完为true，不调stop直接调play则直接播放，没有此消息
	void PlayDelegate_Stop(boolean end, String savePath, int playType);

	void PlayDelegate_SeekSuccess(int pos, int playType);

	// 非网络歌曲bufferPos=total
	void PlayDelegate_PlayProgress(int total, int playPos, int bufferPos);

	// 数据不够，暂停播放等待缓冲
	void PlayDelegate_WaitForBuffering();

	// 缓冲完毕继续播放
	void PlayDelegate_WaitForBufferingFinish();

	//缓存下载进度
	void PlayDelegate_CacheProgress(int currentProgress, int fileLenTotal);
	
	// 整首歌曲下载完毕
	void PlayDelegate_DownloadFinished(final String savePath, long id);

	// 0-100，如果当前是mute模式，设置一个大于0的vol会先收到SetMute（false）消息，再收到这个消息
	void PlayDelegate_SetVolume(int vol);

	// 收到这个消息之后会接着收到一个SetVolume消息，mute=true时候vol=0，mute=false时候vol=之前设置mute=true时候的音量值
	void PlayDelegate_SetMute(boolean mute);
	
	void PlayDelegate_OnRestart();
	// 这里接收频谱左右声道数据
	void PlayDelegate_onFFTDataReceive(float[] leftFFT, float[] rightFFT);
}
