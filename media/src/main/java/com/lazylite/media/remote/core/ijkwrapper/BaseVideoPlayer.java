package com.lazylite.media.remote.core.ijkwrapper;

import android.view.SurfaceHolder;

public abstract class BaseVideoPlayer {

	public static final int	STATE_STOPPED	= 0;
	public static final int	STATE_PREPARING	= 1;
	public static final int	STATE_PREPARED	= 2;
	public static final int	STATE_PAUSED	= 3;
	public static final int	STATE_PLAYING	= 4;
	public static final int	STATE_COMPLETED	= 5;

	private final Object mPlayStateLock	= new Object();

	private int				mPlayState		= STATE_STOPPED;

	protected boolean		mAutoPlay		= true;

	public BaseVideoPlayer() {
	}

	public void setAutoPlay(boolean autoPlay) {
		this.mAutoPlay = autoPlay;
	}

	public void stop() {
		reset();
	}

	public abstract void pause();

	public abstract void resume();

	public abstract boolean play(String path);

	public abstract void reset();

	public abstract void release();

	public abstract int getCurrentPosition();

	public abstract void seekTo(int position);

	public abstract int getDuration();

	public int getPlayState() {
		synchronized (mPlayStateLock) {
			return mPlayState;
		}
	}

	public void setPlayState(int state) {
		int ps;
		synchronized (mPlayStateLock) {
			ps = mPlayState;
			mPlayState = state;
		}

		if (ps != state && mOnStateChangedListener != null) {
			mOnStateChangedListener.onStateChanged(this);
		}
	}

	public boolean isPlaying() {
		return getPlayState() == STATE_PLAYING;
	}

	public boolean isPreparing() {
		return getPlayState() == STATE_PREPARING;
	}

	public boolean isPrepared() {
		return getPlayState() >= STATE_PREPARED;
	}

	public boolean isPaused() {
		return getPlayState() == STATE_PAUSED;
	}

	public boolean isStopped() {
		return getPlayState() == STATE_STOPPED
				|| getPlayState() == STATE_COMPLETED;
	}

	public boolean isComplete() {
		return getPlayState() == STATE_COMPLETED;
	}

	public interface OnPreparedListener {
		void onPrepared(BaseVideoPlayer pc);
	}

	protected OnPreparedListener	mOnPreparedListener	= null;

	public void setOnPreparedListener(OnPreparedListener listener) {
		this.mOnPreparedListener = listener;
	}

	public interface OnCompletionListener {
		void onCompletion(BaseVideoPlayer pc);
	}

	protected OnCompletionListener	mOnCompletionListener	= null;

	public void setOnCompletionListener(OnCompletionListener listener) {
		this.mOnCompletionListener = listener;
	}

	public interface OnErrorListener {
		boolean onError(BaseVideoPlayer pc, int what, int extra);
	}

	protected OnErrorListener	mOnErrorListener	= null;

	public void setOnErrorListener(OnErrorListener listener) {
		this.mOnErrorListener = listener;
	}

	public interface OnStateChangedListener {
		void onStateChanged(BaseVideoPlayer pc);
	}

	protected OnStateChangedListener	mOnStateChangedListener	= null;

	public void setOnStateChangedListener(OnStateChangedListener listener) {
		this.mOnStateChangedListener = listener;
	}

	public final static int	MEDIA_INFO_LOG				= 0;
	public final static int	MEDIA_INFO_BITRATE			= 1;
	public final static int	MEDIA_INFO_DURATION			= 2;
	public final static int	MEDIA_INFO_CHANNEL			= 3;
	public final static int	MEDIA_INFO_SAMPLERATE		= 4;
	public final static int	MEDIA_INFO_SAMPLEPERFRAME	= 5;

	public interface OnInfoListener {
		void onInfo(BaseVideoPlayer pc, int what, int extra);
	}

	protected OnInfoListener	mOnInfoListener	= null;

	public void setOnInfoListener(OnInfoListener listener) {
		this.mOnInfoListener = listener;
	}

	public interface OnBufferingUpdateListener {
		void onBufferingUpdate(BaseVideoPlayer pc, int percent);
	}

	protected OnBufferingUpdateListener	mOnBufferingUpdateListener	= null;

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
		this.mOnBufferingUpdateListener = listener;
	}

	public interface OnVideoSizeChangedListener {
		void onVideoSizeChanged(BaseVideoPlayer pc, int width, int height);
	}

	protected OnVideoSizeChangedListener	mOnVideoSizeChangedListener	= null;

	public void setOnVideoSizeChangedListener(
			OnVideoSizeChangedListener listener) {
		this.mOnVideoSizeChangedListener = listener;
	}

	public abstract int getVideoHeight();

	public abstract int getVideoWidth();

	public abstract int getBufferPercentage();

	public abstract void setDisplay(SurfaceHolder sh);
}
