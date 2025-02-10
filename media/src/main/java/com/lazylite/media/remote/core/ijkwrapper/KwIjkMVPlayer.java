package com.lazylite.media.remote.core.ijkwrapper;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import org.ijkplayer.IMediaPlayer;
import org.ijkplayer.IjkMediaPlayer;

import java.io.IOException;


/**
 * Created by Administrator on 2016/5/9.
 */
public class KwIjkMVPlayer extends BaseVideoPlayer {
    private static final String TAG	                        = "KwIjkMVPlayer";

    public static KwIjkMVPlayer createInstance()
    {
        return new KwIjkMVPlayer();
    }

    public static final int	        STATE_ERROR					= -1;
    public static final int	        STATE_IDLE  				= 0;
    public static final int	        STATE_PREPARING	            = 1;
    public static final int	        STATE_PREPARED	            = 2;
    public static final int	        STATE_PAUSED	            = 3;
    public static final int	        STATE_PLAYING	            = 4;
    public static final int	        STATE_PLAYBACK_COMPLETED    = 5;

    public static final int         MEDIA_ERROR_INDA            = 0x41444E49;   //Invalid data found when processing input

    private Context mContext;
    private SurfaceHolder mSurfaceHolder				= null;
    private int						mSeekWhenPrepared;
    private int						mCurrentBufferPercentage;
    private int						mCurrentState				= STATE_IDLE;
    private int						mTargetState				= STATE_IDLE;
    private int						mVideoHeight;
    private int						mVideoWidth;
    private int					    mDuration;
    private Uri mUri;
    protected boolean				isBufferCount				= true;
    private boolean					mCanPause;
    private boolean					mCanSeekBack;
    private boolean					mCanSeekForward;
    private IjkMediaPlayer mIjkPlayer                  = null;

    public KwIjkMVPlayer() {
    }

    // must called by user
    public void setVideoContext(Context context) {
        this.mContext = context;
    }

    // must called by user
    public boolean isInitForContext(Context context) {
        if (mContext != null && mContext == context) return true;
        return false;
    }

    private boolean isInPlaybackState() {
        if((mIjkPlayer != null) && (mCurrentState != STATE_ERROR)
                && (mCurrentState != STATE_IDLE)
                && (mCurrentState != STATE_PREPARING))
            return true;
        return false;
    }

    private void openVideo() {
        if ((this.mUri == null) || (this.mSurfaceHolder == null) || (mContext == null) )
            return;
        release(false);
        try {
            mIjkPlayer = new IjkMediaPlayer();
            setEventListener();
            //提高解码画面质量
            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0 /*48*/);
//            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "mediacodec-all-videos", 1);
            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);

            //设置启动缓冲时间
            mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "first-high-water-mark-ms",     2000);

            //5.1音效和audiotrack有冲突的特殊手机型号采用opensles,如：Redmi 3S
            if (!TextUtils.isEmpty(Build.MODEL) && (Build.MODEL.equalsIgnoreCase("Redmi 3S") || Build.MODEL.equalsIgnoreCase("Redmi 3X"))) {
                mIjkPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
            }

            mDuration = -1;
            mCurrentBufferPercentage = 0;
            mIjkPlayer.setDataSource(mContext, mUri);
            mIjkPlayer.setDisplay(mSurfaceHolder);
            mIjkPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mIjkPlayer.setScreenOnWhilePlaying(true);
            mIjkPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            setPlayState(mCurrentState);
        } catch (IOException localIOException) {
            Log.w(TAG, "Unable to open content: " + this.mUri,
                    localIOException);
            this.mCurrentState = STATE_ERROR;
            this.mTargetState = STATE_ERROR;
            setPlayState(this.mCurrentState);
//???            this.mErrorListener.onError(mIjkPlayer,
//???                    mIjkPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalArgumentException localIllegalArgumentException) {
            Log.w(this.TAG, "Unable to open content: " + this.mUri,
                    localIllegalArgumentException);
            this.mCurrentState = STATE_ERROR;
            this.mTargetState = STATE_ERROR;
            setPlayState(this.mCurrentState);
//???            this.mErrorListener.onError(this.mMediaPlayer,
//???                    MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        }

    }

    private void setEventListener()
    {
        if( null != mIjkPlayer ) {
            mIjkPlayer.setOnPreparedListener(mPreparedListener);
            mIjkPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mIjkPlayer.setOnCompletionListener(mCompletionListener);
            mIjkPlayer.setOnErrorListener(mErrorListener);
            mIjkPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mIjkPlayer.setOnInfoListener(mInfoListener);
            mIjkPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
        }
    }

    private void release(boolean paramBoolean)
    {
        if(this.mIjkPlayer != null)
        {
            this.mIjkPlayer.stop();
            this.mIjkPlayer.release();
            this.mIjkPlayer = null;
            this.mCurrentState = STATE_IDLE;
            if(paramBoolean)
                this.mTargetState = STATE_IDLE;
            setPlayState(mCurrentState);
            mCanSeekForward = false;
            mCanSeekBack = false;
            mCanPause = false;
            mCurrentBufferPercentage = 0;
        }
    }

    public boolean canPause() {
        return mCanPause;
    }

    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    public boolean ismCanSeekBack() {
        return mCanSeekBack;
    }

    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }

    public int getCurrentPosition() {
        if(isInPlaybackState())
            return (int)mIjkPlayer.getCurrentPosition();
        return 0;
    }

    public int getDuration() {
        if(isInPlaybackState()) {
            if( mDuration <= 0)
                mDuration = (int)mIjkPlayer.getDuration();
        }
        else
            mDuration = -1;
        return (int)mDuration;
    }

    public boolean isPlaying() {
        if(isInPlaybackState())
            return mIjkPlayer.isPlaying();
        return false;
    }

    public void pause() {
        if((isInPlaybackState()) && mIjkPlayer.isPlaying())
        {
            mIjkPlayer.pause();
            mCurrentState = STATE_PAUSED;
            setPlayState(mCurrentState);
        }
        mTargetState = STATE_PAUSED;
    }

    public void seekTo(int msec) {
        if(isInPlaybackState())
        {
            mIjkPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        }
        else
        {
            mSeekWhenPrepared = msec;
        }
    }

    public int getTargetState()
    {
        return mTargetState;
    }

    public void setVideoURI(Uri uri)
    {
        mUri = uri;
        mSeekWhenPrepared = 0;
        openVideo();
    }

    public void setVideoPath(String path)
    {
        setVideoURI(Uri.parse(path));
    }

    public void start() {
        if(isInPlaybackState()) {
            if((mSeekWhenPrepared > 0))
                seekTo(mSeekWhenPrepared);
            mIjkPlayer.start();
            mCurrentState = STATE_PLAYING;
            setPlayState(mCurrentState);
        }
        mTargetState = STATE_PLAYING;
    }

    public void stopPlayback() {
        release(true);
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
    }

    @Override
    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        if (holder != null) {
            openVideo();
        } else {
            release();
        }
    }

    @Override
    public void resume() {
        start();
    }

    @Override
    public boolean play(String path) {
        reset();
        setVideoPath(path);
        start();
        return true;
    }

    @Override
    public void reset() {
        stopPlayback();
        setAutoPlay(true);
    }

    @Override
    public void release() {
        release(true);
    }

    public int getAudioSessionId() {
        return 0;
    }

    /**
     * Callback：视频大小改变
     */
    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height,
                                       int sarNum, int sarDen) {
            Log.i(TAG, "onVideoSizeChanged: (%d" + width + "x%d)" + height);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mOnVideoSizeChangedListener != null) {
                mOnVideoSizeChangedListener.onVideoSizeChanged(KwIjkMVPlayer.this, width, height);
            }
        }
    };

    /**
     * Callback：播放器准备完成
     * Not Used Now
     */
    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            Log.i(TAG, "onPrepared");
            mCurrentState = STATE_PREPARED;
            setPlayState(mCurrentState);
            mTargetState = STATE_PLAYING;
            mCanSeekForward = true;
            mCanSeekBack = true;
            mCanPause = true;
            if((mSeekWhenPrepared > 0))
                seekTo(mSeekWhenPrepared);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(KwIjkMVPlayer.this);
            }
        }
    };

    /**
     * Callback：播放完成
     */
    private IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        public void onCompletion(IMediaPlayer mp) {
            Log.i(TAG, "onCompletion");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            setPlayState(mCurrentState);
            mTargetState = STATE_PLAYBACK_COMPLETED;
//            callBack.onPlayerStopped();
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(KwIjkMVPlayer.this);
            }
        }
    };

    /**
     * Callback：播放错误
     */
    private IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
        public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) throws IOException {
            Log.i(TAG, "Error: %d" + framework_err +", %d!" + impl_err);
            mCurrentState = STATE_ERROR;
            setPlayState(mCurrentState);
            mTargetState = STATE_ERROR;

//            callBack.onEncounteredError(impl_err);
            if ((mOnErrorListener != null)
                    && (mOnErrorListener.onError(KwIjkMVPlayer.this, framework_err, impl_err)))
                return true;
            return false;
        }
    };

    /**
     * Callback：缓冲进度(0-100)
     * 直播时总是0
     */
    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        }
    };

    /**
     * Callback：?????
     */
    private IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) throws IOException {
            Log.i(TAG, "onInfo: (%d" + arg1 +", %d)" + arg2);

            switch (arg1) {
                case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_TRACK_LAGGING)");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_RENDERING_START)");
                    mCurrentState = STATE_PLAYING;
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_START)");
                    if(mCurrentState != STATE_PAUSED)
                    {
//                        if(!mSeekFlag) {
//                            mCurrentState = STATE_BUFFERING;
//                            callBack.onStartBuffering();
//                        }
                    }
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_END)");
                    if(mCurrentState != STATE_PAUSED)
                    {
                        mCurrentState = STATE_PLAYING;
//                        if(!mSeekFlag)
//                            callBack.onEndBuffering();
                    }
                    break;
                case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2 + ")");
                    break;
                case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_BAD_INTERLEAVING)");
                    break;
                case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_NOT_SEEKABLE)");
                    break;
                case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_METADATA_UPDATE)");
                    break;
                case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_UNSUPPORTED_SUBTITLE)");
                    break;
                case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_SUBTITLE_TIMED_OUT)");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
//                    mVideoRotationDegree = arg2;
                    Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2 + ")");
//                    setVideoRotation(arg2);
                    break;
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    Log.i(TAG, "onInfo: (MEDIA_INFO_AUDIO_RENDERING_START):");
                    mCurrentState = STATE_PLAYING;
//                    callBack.onStartPlaying();
                    break;
                case IMediaPlayer.MEDIA_INFO_AUTOSTOPPED:
                    Log.w(TAG, "onInfo: (MEDIA_INFO_AUTOSTOPPED):");
//                    restartAuto();
                    break;
            }

            return true;
        }
    };

    /**
     * Callback：Seek完成事件
     */
    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            Log.i(TAG, "onSeekComplete");
//            callBack.onSeekComplete();
//            mSeekFlag = false;
        }
    };

}
