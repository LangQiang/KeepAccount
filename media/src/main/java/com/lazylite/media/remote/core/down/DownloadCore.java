package com.lazylite.media.remote.core.down;

import android.text.TextUtils;
import android.util.Log;

import com.lazylite.media.Media;
import com.lazylite.media.remote.core.play.AntiStealing;
import com.lazylite.media.remote.core.strategies.StrategyCreator;
import com.lazylite.media.utils.TsUrlManager;
import com.lazylite.mod.http.mgr.HttpWrapper;
import com.lazylite.mod.http.mgr.IKwHttpFetcher;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.mgr.model.DownReqInfo;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.receiver.sdcard.SDCardUtils;
import com.lazylite.mod.utils.KwFileUtils;
import com.lazylite.mod.utils.KwTimer;

import java.io.File;
import java.io.FileInputStream;

import cn.kuwo.p2p.JNIP2P;


// by haiping
public final class DownloadCore implements AntiStealing.AntiStealingDelegate, KwTimer.Listener, IKwHttpFetcher.DownloadListener {
	private String TAG = "DownloadCore";

	private OnTaskFinishedListener onFinishedListener;
	private ThreadMessageHandler threadHandler;
	private long threadID;
	private Step currentStep;
	private DownloadTask task;
	private long currentTask;
	private AntiStealing antiStealing;
	private ProgressRunner progressRunner = new ProgressRunner();

	private File infoFile;
	private int retryTimes;
	private boolean sendStartNotify;
	private DownloadDelegate.ErrorCode errorCode;
	private KwTimer progressNotifyTimer;
	private long startTaskTime = 0;
	private int totalSize;
	private int currentSize;
	private HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper;

	public interface OnTaskFinishedListener {
		public void onTaskFinished(final DownloadTask task);
	}

	protected DownloadCore(final ThreadMessageHandler handler, final OnTaskFinishedListener fl, final String TAG) {
		threadHandler = handler;
		threadID = handler.getHandler().getLooper().getThread().getId();
		onFinishedListener = fl;
		if (!TextUtils.isEmpty(TAG)) {
			this.TAG = TAG + "_" + this.TAG;
		}
		antiStealing = new AntiStealing(this, TAG);
		progressNotifyTimer = new KwTimer(this);
	}

	protected void start(final DownloadTask t) {
		if (Thread.currentThread().getId()!=threadID) {
			throw new RuntimeException("禁止跨线程调用");
		}
		Log.i(TAG, "start");
		task = t;
		task.running = true;
		if (task.downloadStrategy == null) {
			task.downloadStrategy = StrategyCreator.createStrategy(task.type);
		}
		if (!SDCardUtils.isAvailable()) {
			setError(DownloadDelegate.ErrorCode.NO_SDCARD);
			currentStep = Step.FAILED;
		} else if (task.audioInfo != null) {
			if (TextUtils.isEmpty(task.tempPath)) {
				task.tempPath = task.downloadStrategy.createTempPath(task);
			}
			currentStep = Step.FIND_PART_FILE;
		} else if (task.url != null) {
			if (TextUtils.isEmpty(task.tempPath)) {
				task.tempPath = task.downloadStrategy.createTempPath(task);
			}
			currentStep = Step.REALDOWNLOAD;
		}
		retryTimes = 0;
		process();
	}

	protected void stop() {
		Log.i(TAG, "stop");
		clear();
	}

	protected DownloadProxy.DownType getCurrentDownType() {
		if (task == null) {
			return DownloadProxy.DownType.MIN;
		} else {
			return task.type;
		}
	}

	private void clear() {
		progressNotifyTimer.stop();
		startTaskTime = 0;
		if (httpWrapper != null) {
			httpWrapper.cancel();
			httpWrapper = null;
		}

		if(currentTask != 0){
			JNIP2P.cancel(currentTask);
			currentTask = 0;
		}

		antiStealing.cancel();
		if (task != null) {
			task.running = false;
			task = null;
		}
		infoFile = null;
		setError(DownloadDelegate.ErrorCode.SUCCESS);
		totalSize = 0;
		currentSize = 0;
		currentStep = Step.WAITING;
		sendStartNotify = false;
	}

	private void process() {
		while (true) {
			Step act = processStep();
			if (act == Step.WAITING) {
				break;
			}
			currentStep = act;
		}
	}

	private enum Step {
		FIND_FINISHED_FILE,
		FIND_PART_FILE,
		FIND_DOWNLOAD_URL,
		ANTISTEALING,
		REALDOWNLOAD,
		DOWNFINISH,
		NOTIFYSUCCESS,
		FAILED,
		WAITING,
		AUTOSTOP
	}

	private Step findDownloadUrl() {
		String url = null;
		if (!TextUtils.isEmpty(task.audioInfo.resUrl)) {
			if (task.audioInfo.resUrl.startsWith("http")) {
				url = task.audioInfo.resUrl;
			} else {
				url = TsUrlManager.getResUrl(task.audioInfo.albumId, task.audioInfo.resUrl);
			}
		}

		if (!TextUtils.isEmpty(url)) {
			task.url = url;
			return Step.REALDOWNLOAD;
		} else {
			return Step.ANTISTEALING;
		}

	}

	private Step findFinishedFile() {

		return Step.FIND_PART_FILE;
	}

	private Step findPartFile() {
		if(!NetworkStateUtil.isAvailable()) {
			setError(DownloadDelegate.ErrorCode.NO_NET);
			return Step.FAILED;
		}

		if (NetworkStateUtil.isOnlyWifiConnect()) {
			setError(DownloadDelegate.ErrorCode.ONLYWIFI);
			return Step.FAILED;
		}
		if (task.type == DownloadProxy.DownType.TINGSHU || task.type == DownloadProxy.DownType.TSPREFETCH) {
			task.tempPath = DownCacheMgr.getUnFinishedTingshu(task.audioInfo.albumId, task.audioInfo.rid, task.format, task.audioInfo.musicMd5);
			if (TextUtils.isEmpty(task.tempPath)) {
				task.tempPath = task.downloadStrategy.createTempPath(task);
			}
			return Step.FIND_DOWNLOAD_URL;
		}

		return Step.ANTISTEALING;
	}


	private Step antiStealing() {
		String oldAntiStealingsig = null;
		if (task.tempPath != null) {
			oldAntiStealingsig = DownCacheMgr.getAntiStealingSig(task.tempPath);
		}
		antiStealing.request(task, oldAntiStealingsig);
		return Step.WAITING;
	}

	private Step realDownload() {
		if (!NetworkStateUtil.isAvailable()) {
			setError(DownloadDelegate.ErrorCode.NO_NET);
			return Step.FAILED;
		}
		infoFile = DownCacheMgr.findInfoFile(task.tempPath);
		int continuePos = 0;
		if (infoFile != null) {
			continuePos = DownCacheMgr.getContinuePos(infoFile);
			currentSize = continuePos;
			if (continuePos > 0 && continuePos == DownCacheMgr.getSavedTotalSize(task.tempPath)) {
				return Step.DOWNFINISH;
			}
		}
		String tempDir = KwFileUtils.getFilePath(task.tempPath);
		if (!KwFileUtils.isExist(tempDir)) {
			KwFileUtils.mkdir(tempDir);
		}
		if (isNoSpace(1024 * 1024)) {
			setError(DownloadDelegate.ErrorCode.NOSPACE);
			return Step.FAILED;
		}


		currentTask = 0;
		startTaskTime = 0;
		//下载播放启动timer,重置startTaskTime
		if ((task.type == DownloadProxy.DownType.TSPREFETCH || task.type == DownloadProxy.DownType.TINGSHU) /*&& retryTimes <= 1*/) {
			startTaskTime = System.currentTimeMillis();
			if (!progressNotifyTimer.isRunnig()) {
				progressNotifyTimer.start(100);

			}
		}

		//上层http请求
		if (currentTask == 0) {
			httpWrapper = KwHttpMgr.getInstance().getKwHttpFetch().asyncDownload(
					new DownReqInfo(task.url, task.tempPath, continuePos), threadHandler.getHandler(), this);
		}
		return Step.WAITING;
	}


	private Step downFinish() {
		task.savePath = task.downloadStrategy.createSavePath(task);
		String saveDir = KwFileUtils.getFilePath(task.savePath);
		if (!KwFileUtils.isExist(saveDir)) {
			KwFileUtils.mkdir(saveDir);
		}
		long tempFileSize = KwFileUtils.getFileSize(task.tempPath);
		if (!task.downloadStrategy.onSuccess(task)) {
			setError(DownloadDelegate.ErrorCode.IO_ERROR);
			return Step.FAILED;
		}

		return Step.NOTIFYSUCCESS;
	}

	private Step notifySuccess() {
		if (task.delegate != null) {
			final DownloadTask t = task;
			notifyDelegate(new MessageManager.Runner() {
				@Override
				public void call() {
					try {
						t.delegate.DownloadDelegate_Finish(t.taskID, DownloadDelegate.ErrorCode.SUCCESS.ordinal(), t.savePath);
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
			});
		}
		return Step.AUTOSTOP;
	}

	private Step downFailed() {
		if (errorCode == DownloadDelegate.ErrorCode.IO_ERROR) {
			if (isNoSpace(16 * 1024)) {
				errorCode = DownloadDelegate.ErrorCode.NOSPACE;
			}
		}
		if (errorCode == DownloadDelegate.ErrorCode.SUCCESS) {
			errorCode = DownloadDelegate.ErrorCode.OTHERS;
		}

		if (task.delegate != null) {
			final DownloadTask t = task;
			final DownloadDelegate.ErrorCode err = errorCode;
			notifyDelegate(new MessageManager.Runner() {
				@Override
				public void call() {
					try {
						t.delegate.DownloadDelegate_Finish(t.taskID, err.ordinal(), null);
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
			});
		}
		return Step.AUTOSTOP;
	}

	private Step autoStop() {
		onFinishedListener.onTaskFinished(task);
		clear();
		return Step.WAITING;
	}

	private Step lastStep;

	private Step processStep() {
		if (lastStep != currentStep) {
			Log.i(TAG, "Step " + currentStep);
			lastStep = currentStep;
		} else {
			//Log.v(TAG, "Step "+currentStep);
		}

		switch (currentStep) {
			case FIND_FINISHED_FILE:
				return findFinishedFile();
			case FIND_PART_FILE:
				return findPartFile();
			case FIND_DOWNLOAD_URL:
				return findDownloadUrl();
			case ANTISTEALING:
				return antiStealing();
			case REALDOWNLOAD:
				return realDownload();
			case DOWNFINISH:
				return downFinish();
			case NOTIFYSUCCESS:
				return notifySuccess();
			case FAILED:
				return downFailed();
			case AUTOSTOP:
				return autoStop();
			default:
				break;
		}
		return Step.AUTOSTOP;
	}

	private void notifyStart(final int size,final int currentLen, final int bitrate, final DownloadDelegate.DataSrc dataSrc) {
		if (!task.started && task.delegate != null) {
			final DownloadTask t = task;
			notifyDelegate(new MessageManager.Runner() {
				@Override
				public void call() {
					try {
						t.delegate.DownloadDelegate_Start(t.taskID, t.url, t.tempPath, size, currentLen, bitrate, dataSrc.ordinal());
					} catch (Throwable e) {
						Log.e(TAG, e.getMessage() + "");
					}
				}
			});
		}
		task.started = true;
	}

	private void notifyDelegate(final MessageManager.Runner r) {
		MessageManager.getInstance().syncRunTargetHandler(
				task.targetHandler == null ? Media.mainHandler : task.targetHandler, r);
	}

	private boolean isNoSpace(final int minSize) {
		return KwFileUtils.getAvailableExternalMemorySize() < minSize;
	}

	private void setError(final DownloadDelegate.ErrorCode err) {
		errorCode = err;
		Log.w(TAG, "down failed,err=" + err);
	}

	private boolean checkData() {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(new File(task.tempPath));
			try {
				int len = Math.min(fin.available(), 50);
				String head = DownloadIOUtils.readString(fin, len).toString();
				if (head.indexOf("html")!=-1
						|| head.indexOf("http")!=-1
						|| head.indexOf("style")!=-1) {
					return false;
				}
			} finally {
				fin.close();
			}
		} catch (Throwable e) {}
		return true;
	}

	@Override
	public void onAntiStealingFinished(final AntiStealing.AntiStealingResult result, boolean success) {
		if (task==null) {

			return;
		}
		if (success) {
			Log.i(TAG, "Antistealing success");
			task.antiResult = result;
			task.url = result.url;
			task.format = result.format;
			task.bitrate = result.bitrate;

			//听书的单独处理
			if (task.type == DownloadProxy.DownType.TINGSHU || task.type == DownloadProxy.DownType.TSPREFETCH) {
				task.audioInfo.bitrate = task.bitrate;
				if (!task.tempPath.endsWith("." + task.format + "." + DownCacheMgr.UNFINISHED_CACHE_EXT)) {
					task.tempPath = task.downloadStrategy.createTempPath(task);
				}
				currentStep = Step.REALDOWNLOAD;
				process();
				return;
			}

		} else {
			Log.w(TAG, "Antistealing failed");
			setError(DownloadDelegate.ErrorCode.ANTISTEALING_FAILED);
			currentStep = Step.FAILED;
		}
		process();
	}


	// 用来计算 SPEEDSLOT_SIZE 秒内的平均速度，避免界面上速度不停大幅度跳动，且启动任务时候有个逐渐递增过程
	private static final int SPEEDSLOT_SIZE = 5;
	private int[] speedSlot = new int[SPEEDSLOT_SIZE];
	private int nextSpeedSlotPos;
	private float speed = 0;

	@Override
	public void onTimer(final KwTimer timer) {
		notifyDownloadProgress();
		//通过timer计时控制没有下载进度的情况
		checkProgressTimeout();
	}

	//5秒超时计算
	private boolean isTimeout() {
		if (startTaskTime > 0 && (System.currentTimeMillis() - startTaskTime) > 5000){
			return true;
		}
		return false;
	}
	private void checkProgressTimeout() {
		//歌曲底层socket下载的无进度的异常处理
		if ((task.type == DownloadProxy.DownType.TINGSHU || task.type == DownloadProxy.DownType.TSPREFETCH) /* && retryTimes <= 1*/) {
			if (speed > 0 && startTaskTime > 0){//重新计算开始时间
				startTaskTime = System.currentTimeMillis();
			}
			//无进度或者下载速度为0
			if ((currentSize == 0 || speed == 0) && isTimeout()) {
				startTaskTime = 0;
				httpWrapper.cancel();
				onError(90002, null, httpWrapper);
			}
		}
	}

	private void notifyDownloadProgress(){
		if (task.delegate != null) {
			speedSlot[nextSpeedSlotPos] = currentSize;
			++nextSpeedSlotPos;
			if (nextSpeedSlotPos >= SPEEDSLOT_SIZE) {
				nextSpeedSlotPos = 0;
			}
			speed = ((float) (currentSize - speedSlot[nextSpeedSlotPos]) * 2/ SPEEDSLOT_SIZE) / 1024; // 1s一个tick
			if (speed < 0) { // 当缓存文件出现问题，http内部重新下载的时候，可能下载进度回退，带来负数
				speed = 0;
			}
			notifyDelegate(progressRunner.pack(task, totalSize, currentSize, speed));
		}
	}



	static final class ProgressRunner extends MessageManager.Runner {
		public ProgressRunner pack(final DownloadTask t, final int ts, final int cs, final float sp) {
			task = t;
			totalSize = ts;
			currentSize = cs;
			speed = sp;
			return this;
		}

		@Override
		public void call() {
			try {
				task.delegate.DownloadDelegate_Progress(task.taskID, totalSize, currentSize, speed);
			} catch (Throwable e) {
				Log.e("DownloadCore", e.getMessage() + "");
			}
		}

		DownloadTask task;
		int totalSize;
		int currentSize;
		float speed;
	}

	@Override
	public void onComplete(HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
		Log.i(TAG, "http down finish retryTimes:" + retryTimes);
		if (!checkData()) {
			KwFileUtils.deleteFile(task.tempPath);
			onError(90001, "invalid data", httpWrapper);
		} else {
			this.httpWrapper = null;
			startTaskTime = 0;
			currentStep = Step.DOWNFINISH;
			process();
		}
	}

	@Override
	public void onError(int errorCode, String msg, HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
		if (!this.httpWrapper.getTraceId().equals(httpWrapper.getTraceId())) {
			return;
		}
		this.httpWrapper = null;
		startTaskTime = 0;
		boolean noSpace = isNoSpace(16 * 1024);
		if (noSpace || retryTimes >= 2) {
			if (noSpace) {
				setError(DownloadDelegate.ErrorCode.NOSPACE);
			} else {
				setError(DownloadDelegate.ErrorCode.NET_CDN_ERROR);
			}
			currentStep = Step.FAILED;
		} else {
			progressNotifyTimer.stop();
			++retryTimes;
			Log.i(TAG, "http down failed retryTimes:" + retryTimes);
			currentStep = Step.REALDOWNLOAD;
		}
		process();
	}

	@Override
	public void onStart(long startPos, long totalLength, HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
		if (task==null) {
			setError(DownloadDelegate.ErrorCode.OTHERS);
			currentStep = Step.FAILED;
			process();
			return;
		}
		int totalSize = (int) totalLength;
		Log.i(TAG, "http down start retryTimes:"+retryTimes);
		if (isNoSpace(totalSize)) {
			setError(DownloadDelegate.ErrorCode.NOSPACE);
			currentStep = Step.FAILED;
			process();
		} else {
			if (infoFile == null || !infoFile.exists()) {
				infoFile = DownCacheMgr.createInfoFile(task.tempPath, task.type, totalSize);
			}
			if (task.delegate != null && !sendStartNotify) {
				DownloadDelegate.DataSrc src = currentSize > 0 ? DownloadDelegate.DataSrc.LOCAL_PART
						: DownloadDelegate.DataSrc.NET;


				sendStartNotify = true;
				notifyStart(totalSize, currentSize, task.antiResult == null ? 0 : task.bitrate, src);
			}

			this.totalSize = totalSize;


			// 开始新任务需要先清零
			for (int i = 0; i < SPEEDSLOT_SIZE; i++) {
				speedSlot[i] = 0;
			}
			nextSpeedSlotPos = 0;
		}
	}

	@Override
	public void onProgress(long currentPos, long totalLength, HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {
		DownCacheMgr.saveContinuePos(infoFile, task.type, (int) currentPos);
		this.currentSize = (int) currentPos;
		if(!progressNotifyTimer.isRunnig() && this.httpWrapper != null){
			progressNotifyTimer.start(100);
		}
	}

	@Override
	public void onCancel(HttpWrapper<IKwHttpFetcher.DownloadListener> httpWrapper) {

	}

}