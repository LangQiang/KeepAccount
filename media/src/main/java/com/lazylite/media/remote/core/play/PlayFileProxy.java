package com.lazylite.media.remote.core.play;

import android.os.RemoteException;
import android.text.TextUtils;

import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.remote.core.down.DownCacheMgr;
import com.lazylite.media.remote.core.down.DownloadDelegate;
import com.lazylite.media.remote.core.down.DownloadMgr;
import com.lazylite.media.remote.core.down.DownloadProxy;
import com.lazylite.media.remote.AIDLDownloadDelegate;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.mod.utils.KwFileUtils;

import cn.kuwo.p2p.FileServerJNI;


public final class PlayFileProxy {

	public static PlayFileProxy getInstance() {
		return instance;
	}

	public void init(final ThreadMessageHandler h) {
		msgHandler = h;
		FileServerJNI.init(0);
	}

	// 代理一个本地歌曲，返回本地代理url，可直接播放，并可完整seek，失败返回null
	public String startLocal(final String filePath) {
		cancel();

		if (!KwFileUtils.isExist(filePath)) {
			return null;
		}
		int fileSize = (int) KwFileUtils.getFileSize(filePath);
		if (fileSize == 0) {
			return null;
		}

		String fileName = KwFileUtils.getFullFileNameByPath(filePath);
		FileServerJNI.setFile(filePath, fileName, fileSize, fileSize, 0, null);
		currentFile = filePath;
		return FileServerJNI.getUrl(filePath);
	}

	// 代理一个本地歌曲，返回本地代理url，可直接播放，并可完整seek，失败返回null
	public String startLocal(final String filePath, int isKwPoco, final String uid) {
		cancel();

		if (!KwFileUtils.isExist(filePath)) {
			return null;
		}
		int fileSize = (int) KwFileUtils.getFileSize(filePath);
		if (fileSize == 0) {
			return null;
		}

		String fileName = KwFileUtils.getFullFileNameByPath(filePath);
		FileServerJNI.setFile(filePath, fileName, fileSize, fileSize, isKwPoco, uid);
		currentFile = filePath;
		return FileServerJNI.getUrl(filePath);
	}

	// 代理一个网络文件，无返回值，等到start消息的时候可以拿到本地代理url开始播放
	// 开始通知里的tempPath为本地代理url

	public void startNet(final AudioInfo audioInfo,
						 final AIDLDownloadDelegate delegate) {
		cancel();
		playerDelegate = delegate;
		waitingSetFile = true;
		curDownloadStep = DownTaskStep.STARTDOWN;
		downTaskID = DownloadMgr.getInstance(DownloadProxy.DownGroup.TINGSHU)
				.addTask(
						audioInfo,
						DownloadProxy.DownType.TINGSHU,
						downloadDelegate, msgHandler.getHandler());
	}

	public void cancel() {
		if (!TextUtils.isEmpty(currentFile)) {
			FileServerJNI.delFile(currentFile);
			currentFile = null;
		}
		if (downTaskID > 0) {
			DownloadMgr.removeTask(downTaskID);
			downTaskID = 0;
		}
		playerDelegate = null;
		curDownloadStep = DownTaskStep.NOTSTART;
	}


	//移除下载任务，停止下载
	public void removeTask() {
		if (downTaskID > 0) {
			DownloadMgr.removeTask(downTaskID);
			downTaskID = 0;
		}
	}

	
	private DownloadDelegate downloadDelegate = new DownloadDelegate() {
		@Override
		public void DownloadDelegate_Start(final int id, final String url,
                                           final String tempPath, final int totalLen,
                                           final int currentLen, final int bitrate,
                                           final DownloadDelegate.DataSrc dataSrc) {
			
			curDownloadStep = DownTaskStep.CALLBACK_DOWNSTART;

			PlayFileProxy.this.currentFile = tempPath;
			PlayFileProxy.this.url = url;
			PlayFileProxy.this.totalLen = totalLen;
			PlayFileProxy.this.currentLen = currentLen;
			PlayFileProxy.this.bitrate = bitrate;
			PlayFileProxy.this.dataSrc = dataSrc;

			if (KwFileUtils.isExist(tempPath) && currentLen>0) { // 保证有文件才set，否则等
				setFile();
			}
		}

		@Override
		public void DownloadDelegate_Progress(int id, int totalLen,
				int current, float speed) {
			curDownloadStep = DownTaskStep.CALLBACK_PROGRESS;
			
			if (current <= 0 || TextUtils.isEmpty(currentFile)) {
				return;
			}
			
			PlayFileProxy.this.currentLen = current;
			
			if(waitingSetFile) { // 上面start里没有文件，这里才有文件了，set一下
				if(currentFile != null){
					setFile();
				}
			} else {
				FileServerJNI.updateFile(currentFile, current);
				try {
					playerDelegate.DownloadDelegate_Progress(id, totalLen, current,
							speed);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void DownloadDelegate_Finish(int id,
				final DownloadDelegate.ErrorCode err, final String savePath) {
			if (err == DownloadDelegate.ErrorCode.SUCCESS) {
				currentLen = (int) KwFileUtils.getFileSize(savePath);
				curDownloadStep = DownTaskStep.CALLBACK_SUCCESS;
				
				if (waitingSetFile) { // 文件太小，连progress也跳过了，还没set呢，先set一下
					currentFile = savePath;
					setFile();
				} else {
					if(!TextUtils.isEmpty(currentFile)){
						FileServerJNI.updateFile(currentFile,
								currentLen);
					}
				}
			} else {
				curDownloadStep = DownTaskStep.CALLBACK_FIAL;
				
				/*
				if (waitingSetFile) { // 没发set就失败了，要先发一下start保证下游收到finish之前一定start过了
					try {
						playerDelegate.DownloadDelegate_Start(0, currentFile,
								currentFile, totalLen, currentLen,
								bitrate, dataSrc.ordinal());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}*/
			}
			try {
				playerDelegate.DownloadDelegate_Finish(id, err.ordinal(),
						savePath);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		private void setFile() {
			waitingSetFile = false;
			
			String musicFormatStr = DownCacheMgr.getSongFormat(currentFile);
			if (TextUtils.isEmpty(musicFormatStr)) {
				musicFormatStr = "aac";
			}
			
			String fileName = KwFileUtils.getFullFileNameByPath(currentFile)
					+ musicFormatStr;
			FileServerJNI.setFile(currentFile, fileName, totalLen, currentLen, 0, null);

			try {
				playerDelegate.DownloadDelegate_Start(0, FileServerJNI.getUrl(currentFile),
					     currentFile,  totalLen, currentLen,
						bitrate, dataSrc.ordinal());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	
	public int getDownTaskID(){
		return downTaskID;
	}
	public DownTaskStep getDownTaskStep(){
		return curDownloadStep;
	}
	
	public void setLaskError(String error){
		playErrorInfo = error;
	}
	public String getLastError(){
		String temp = playErrorInfo;
		playErrorInfo = "";
		return temp;
	}
	private String playErrorInfo="";
	
	public enum DownTaskStep{
		NOTSTART,
		STARTDOWN,
		CALLBACK_DOWNSTART,
		CALLBACK_PROGRESS,
		CALLBACK_SUCCESS,
		CALLBACK_FIAL,
	}

	private static PlayFileProxy instance = new PlayFileProxy();
	private ThreadMessageHandler msgHandler;
	private AIDLDownloadDelegate playerDelegate;
	private int downTaskID;
	private DownTaskStep curDownloadStep= DownTaskStep.NOTSTART;
	
	private String currentFile;
	private String url;
	private int totalLen;
	private int currentLen;
	private int bitrate;
	private DownloadDelegate.DataSrc dataSrc;
	private boolean waitingSetFile;
}
