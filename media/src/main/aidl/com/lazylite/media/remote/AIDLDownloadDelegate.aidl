package com.lazylite.media.remote;

interface AIDLDownloadDelegate {
	void DownloadDelegate_Start(int id, String url, String tempPath, int totalLen
			, int currentLen, int bitrate, int dataSrc);

	void DownloadDelegate_Progress(int id, int totalLen, int current, float speed);
 
	void DownloadDelegate_Finish(int id, int err, String savePath);

}