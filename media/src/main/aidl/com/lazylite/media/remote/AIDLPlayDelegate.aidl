package com.lazylite.media.remote;

interface AIDLPlayDelegate {
	void PlayDelegate_PreStart(boolean buffering);
 
	void PlayDelegate_RealStart(long realTime);

	void PlayDelegate_Pause();

	void PlayDelegate_Continue();

	void PlayDelegate_Failed(int error);

	void PlayDelegate_Stop(boolean end, String savePath, int playType);

    void PlayDelegate_SeekSuccess(int pos, int playType);

	void PlayDelegate_PlayProgress(int total, int playPos, int bufferPos);

	void PlayDelegate_WaitForBuffering();

	void PlayDelegate_WaitForBufferingFinish();

	void PlayDelegate_CacheProgress(int currentProgress, int total);

	void PlayDelegate_DownloadFinished(String savePath, long id);

	void PlayDelegate_SetVolume(int vol);

	void PlayDelegate_SetMute(boolean mute);
	
	void PlayDelegate_OnRestart();

	void PlayDelegate_onFFTDataReceive(in float[] leftFFT,in float[] rightFFT);
}