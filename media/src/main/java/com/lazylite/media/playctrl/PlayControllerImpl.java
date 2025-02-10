package com.lazylite.media.playctrl;

import android.text.TextUtils;

import com.godq.media_api.media.IPlayController;
import com.godq.media_api.media.IPlayObserver;
import com.godq.media_api.media.PlayStatus;
import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;
import com.lazylite.media.bean.AudioInfo;
import com.lazylite.media.remote.core.ijkwrapper.PlayDelegate;
import com.lazylite.mod.App;
import com.lazylite.mod.config.ConfMgr;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.messagemgr.ThreadMessageHandler;
import com.lazylite.play.notification.PlayNotificationMgrImpl;
import com.lazylite.play.recent.RecentBean;
import com.lazylite.play.recent.RecentImpl;

import java.util.ArrayList;
import java.util.List;

public class PlayControllerImpl implements IPlayController {

    private final ThreadMessageHandler playProxyHandler = new ThreadMessageHandler("play_proxy");

    private boolean isInited;

    private boolean isPlayed;

    private PlayProxy playProxy;

    List<ChapterBean> mCurChapters;

    private int mCurPlayPos;

    private BookBean mCurBook;

    private ChapterBean mCurChapter;

    private int mCurPlayDuration;

    private int mCurPlayProgress;

    private int mCurPlayMode = PlayMode.MODE_ALL_ORDER;

    private float mCurSpeed = 1f;

    private final static int MAX_TRY_TIMES = 3;

    private int mTryTime;

    private static final String KEY_PLAY_SPEED = "play_speed";
    private static final String KEY_PLAY_MODE = "play_mode";

    private PlayControllerImpl() {
        if (playProxy == null) {
            playProxy = new PlayProxy(playProxyHandler);
            playProxy.setDelegate(playDelegate);
        }
    }

    private PlayProxy getPlayProxy() {
        if (playProxy == null) {
            playProxy = new PlayProxy(playProxyHandler);
            playProxy.setDelegate(playDelegate);
        }
        return playProxy;
    }

    public static IPlayController getInstance() {
        return Inner.iPlayController;
    }

    private static class Inner {
        private static final IPlayController iPlayController = new PlayControllerImpl();
    }

    public void init() {
        if (isInited) {
            return;
        }
        isInited = true;

        if (mCurChapters == null) {
            mCurChapters = new ArrayList<>(5);
        } else {
            mCurChapters.clear();
        }
        mCurSpeed = ConfMgr.getFloatValue("", KEY_PLAY_SPEED, 1f);

        RecentImpl.getInstance().getLastRecent(recentBeans -> {
            if (recentBeans == null || recentBeans.size() == 0) {
                return;
            }

            RecentBean recentBean = recentBeans.first();

            mCurBook = recentBean.getBookBean();

            mCurPlayPos = 0;
            mCurPlayProgress = recentBean.getProgress();
//            logInfo.progress = mCurPlayProgress;
            if(recentBean.getChapterBean() != null) {
                mCurPlayDuration = recentBean.getChapterBean().mDuration;
                mCurChapters.add(recentBean.getChapterBean());
            }

            if (mCurPlayPos >= 0 && mCurChapters.size() > mCurPlayPos) {
                mCurChapter = mCurChapters.get(mCurPlayPos);
            }
            MessageManager.getInstance().asyncRun(2000, new MessageManager.Runner() {
                @Override
                public void call() {
                    if (isPlayed || mCurChapter == null || mCurBook == null) {
                        return;
                    }
                    PlayNotificationMgrImpl.initNotificationMusic(App.getInstance(), mCurBook.mImgUrl, mCurChapter.mName, mCurChapter.mArtist);
                }
            });
        });
    }

    @Override
    public boolean play(BookBean book, List<? extends ChapterBean> chapters, int index, int startPos) {
        if (book == null || chapters == null || chapters.size() == 0) {
            return false;
        }
        if (index < 0 || index >= chapters.size()) {
            return false;
        }

        if (mCurChapters == null) {
            mCurChapters = new ArrayList<>();
        } else {
            mCurChapters.clear();
        }
        mCurChapters.addAll(chapters);
        ChapterBean chapter = mCurChapters.get(index);
        mCurPlayPos = index;
        return playShowTips(book, chapter, startPos);
    }

    @Override
    public boolean playNext(boolean isAuto) {
        if (mCurChapter == null || mCurChapters.size() == 0) {
            return false;
        }
        if (!isAuto) {
            return looperNext();
        } else {
            if (mCurPlayMode == PlayMode.MODE_ALL_ORDER) {
                if (mCurPlayPos == mCurChapters.size() - 1) {
                    //停止播放
                    //并且发送通知
                    PlayProxy playProxy = getPlayProxy();
                    if (playProxy != null) {
                        playProxy.stop();
                        MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                            @Override
                            public void call() {
                                ob.onStop(true);
                            }
                        });
                        return true;
                    }
                } else {
                    return looperNext();
                }
            } else if (mCurPlayMode == PlayMode.MODE_ALL_CIRCLE) {
                return looperNext();
            } else if (mCurPlayMode == PlayMode.MODE_SINGLE_CIRCLE) {
                return innerPlay(mCurBook, mCurChapter, 0);
            }
        }
        return true;
    }

    @Override
    public void pause() {
        PlayProxy playProxy = getPlayProxy();
        if (playProxy != null) {
            playProxy.pause();
        }
        RecentImpl.getInstance().saveRecent(RecentBean.CreateRecent(getCurrentBook(), getCurrentChapter(), mCurPlayProgress, System.currentTimeMillis()));

    }

    @Override
    public boolean continuePlay() {
        if (getStatus() == PlayProxy.Status.PAUSE) {
            PlayProxy playProxy = getPlayProxy();
            if (playProxy != null) {
                return playProxy.resume();
            } else {
                return false;
            }
        } else if (getStatus() == PlayProxy.Status.INIT
                || getStatus() == PlayProxy.Status.STOP) {
            if (mCurBook != null && mCurChapter != null) {
                innerPlay(mCurBook, mCurChapter, mCurPlayProgress);
                return true;
            }
            return false;
        } else {
            return getStatus() == PlayProxy.Status.PLAYING
                    || getStatus() == PlayProxy.Status.BUFFERING;
        }
    }

    @Override
    public void stop() {
        PlayProxy playProxy = getPlayProxy();
        if (playProxy != null) {
            playProxy.stop();
        }
    }

    @Override
    public int getCachePos() {
        PlayProxy playProxy = getPlayProxy();
        if (playProxy != null) {
            return playProxy.getBufferPos();
        }
        return 0;
    }

    @Override
    public void setPlayMode(int playMode) {
        if (mCurPlayMode != playMode) {
            if (playMode == PlayMode.MODE_ALL_ORDER
                    || playMode == PlayMode.MODE_SINGLE_CIRCLE
                    || playMode == PlayMode.MODE_ALL_CIRCLE
            ) {
                mCurPlayMode = playMode;
            } else {
                mCurPlayMode = PlayMode.MODE_ALL_ORDER;
            }
            ConfMgr.setLongValue("", KEY_PLAY_MODE, mCurPlayMode, false);
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.playModeChanged(mCurPlayMode);
                }
            });
        }
    }

    @Override
    public int getPlayMode() {
        return mCurPlayMode;
    }

    @Override
    public void setSpeed(float speed) {
        PlayProxy playProxy = getPlayProxy();
        if (playProxy == null) {
            return;
        }

        if (speed <= 0) {
            speed = 1.0f;
        }

        playProxy.setSpeed(speed);

        if (mCurSpeed == speed) {
            return;
        }
        mCurSpeed = speed;
        ConfMgr.setFloatValue("", KEY_PLAY_SPEED, mCurSpeed, false);
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.speedChanged(mCurSpeed);
            }
        });
    }

    @Override
    public float getSpeed() {
        return ConfMgr.getFloatValue("", KEY_PLAY_SPEED, 1f);
    }

    @Override
    public int getChaptersPlayIndex() {
        return mCurPlayPos;
    }

    @Override
    public @PlayStatus int getPlayStatus() {
        switch (getStatus()) {
            case INIT:
                return PlayStatus.STATUS_INIT;
            case BUFFERING:
                return PlayStatus.STATUS_BUFFERING;
            case PLAYING:
                return PlayStatus.STATUS_PLAYING;
            case PAUSE:
                return PlayStatus.STATUS_PAUSE;
            case STOP:
                return PlayStatus.STATUS_STOP;
            default:
                return PlayStatus.STATUS_INIT;
        }
    }

    @Override
    public int getDuration() {
        // 初始状态时，听书没有播放，但需要显示上次播放的进度
        if (getStatus() == PlayProxy.Status.INIT && mCurChapter != null) {
            return mCurPlayDuration;
        }
        if (getPlayProxy() != null) {
            return getPlayProxy().getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentProgress() {
        // 初始状态时，听书没有播放，但需要显示上次播放的进度
        if (getStatus() == PlayProxy.Status.INIT && mCurChapter != null) {
            return mCurPlayProgress;
        }
        if (getPlayProxy() != null) {
            return getPlayProxy().getCurrentPos();
        }
        return 0;
    }

    @Override
    public int getPreparePercent() {
        PlayProxy playProxy = getPlayProxy();
        if (playProxy != null) {
            return playProxy.getPreparingPercent();
        } else {
            return 0;
        }
    }

    @Override
    public List<ChapterBean> getPlayList() {
        return mCurChapters;
    }

    @Override
    public BookBean getCurrentBook() {
        return mCurBook;
    }

    @Override
    public ChapterBean getCurrentChapter() {
        return mCurChapter;
    }

    @Override
    public boolean playPre(boolean isAuto) {
        if (mCurChapter == null || mCurChapters.size() == 0) {
            return false;
        }
        return looperPre();
    }

    @Override
    public void clearPlayCache(long albumId, long chapterId) {
        try {
            getPlayProxy().clearCache(new AudioInfo(albumId, chapterId, "", "", "", "", ""));
        } catch (Exception ignore) {

        }

    }

    @Override
    public void clearPlayList() {
        stop();
        mCurChapter = null;
        mCurBook = null;
        mCurChapters.clear();
        mCurPlayDuration = 0;
        mCurPlayProgress = 0;
        MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.clearPlayList();
            }
        });
        try {
            getPlayProxy().clearPlayList();
        } catch (Exception ignore) {

        }
    }

    @Override
    public boolean removeOne(long chapterId) {
        if (mCurChapter == null || mCurChapters == null) {
            return false;
        }
        boolean ret = true;
        if (mCurChapter.mRid == chapterId) {
            if (mCurChapters.size() == 1) {
                clearPlayList();
            } else {
                playNext(false);
                if (removeFromList(chapterId) != -1) {
                    if (mCurPlayPos != 0) {
                        mCurPlayPos --;
                    }
                } else {
                    ret = false;
                }
            }
        } else {
            int index = removeFromList(chapterId);
            if (index != -1) {
                if (index < mCurPlayPos) {
                    mCurPlayPos --;
                }
            } else {
                ret = false;
            }
        }
        return ret;
    }

    private int removeFromList(long chapterId) {
        int ret = -1;
        for (int i = 0; i < mCurChapters.size(); i++) {
            if (mCurChapters.get(i).mRid == chapterId) {
                mCurChapters.remove(i);
                ret = i;
                break;
            }
        }
        return ret;
    }

    @Override
    public void seekTo(int pos) {
        if (getPlayProxy() != null) {
            getPlayProxy().seek(pos);
        }
    }

    private PlayProxy.Status getStatus() {

        if (getPlayProxy() != null) {
            return getPlayProxy().getStatus();
        }
        return PlayProxy.Status.INIT;
    }

    private boolean innerPlay(BookBean book, ChapterBean chapter, int startPos) {
        //为了 播放到最后一集后 再次播放可以继续播
        boolean isNotO = mCurPlayDuration != 0;
        boolean isOver = startPos >= mCurPlayDuration - 1000;
        if (isNotO && isOver){
            boolean isAllOrder = mCurPlayMode == PlayMode.MODE_ALL_ORDER;
            if (mCurChapters!=null && mCurPlayPos == mCurChapters.size() - 1 && isAllOrder){
                startPos = 0;
            }else {
                startPos = mCurPlayDuration - 3000;
            }
        }

        //如果是没有版权的歌曲第一次播放 没办法获取book。
        mCurBook = book;
        mCurChapter = chapter;
        if (TextUtils.isEmpty(mCurChapter.mArtist)) {
            mCurChapter.mArtist = mCurBook.mArtist;
        }
        mCurPlayProgress = 0;
        if (mCurChapter != null) {
            mCurPlayDuration = mCurChapter.mDuration;
        } else {
            mCurPlayDuration = 0;
        }
        return playShowTips(book, chapter, startPos);
    }

    @Override
    public void updateChapterList(List<? extends ChapterBean> chapterBeans) {
        if (chapterBeans == null){
            return;
        }
        int index = chapterBeans.indexOf(mCurChapter);
        if (index != -1){
            if (this.mCurChapters != chapterBeans) {
                this.mCurChapters.clear();
                this.mCurChapters.addAll(chapterBeans);
            }
            mCurChapter = chapterBeans.get(index);
            mCurPlayPos = index;
        } else {
            stop();
            if (!chapterBeans.isEmpty()){
                mCurChapter = chapterBeans.get(0);
                mCurPlayPos = 0;
                play(mCurBook, chapterBeans, mCurPlayPos, 0);
            }
        }
    }

    private void setDownFilePath(ChapterBean chapter) {
    }

    private void prefetchNext() {
        ChapterBean bean;
        //循环播放无法判断mCurChapters里面的第一个是不是实际专辑的第一首歌，所以同MODE_ALL_ORDER一样处理
        if (mCurPlayMode == PlayMode.MODE_ALL_ORDER || mCurPlayMode == PlayMode.MODE_ALL_CIRCLE) {
            int next = mCurPlayPos + 1;
            if (next >= mCurChapters.size()) {
                return;
            }
            bean = mCurChapters.get(next);
            PlayProxy playProxy = getPlayProxy();
            if (playProxy != null) {
                setDownFilePath(bean);
                playProxy.prefetch(new AudioInfo(mCurBook.mBookId, bean.mRid, bean.mName, bean.mArtist, bean.mImgUrl, bean.md5, bean.resUrl));
            }
        }
    }

    private boolean looperNext() {
        if (mCurPlayPos < mCurChapters.size() - 1 && mCurPlayPos >= 0) {
            mCurPlayPos++;
        } else {
            mCurPlayPos = 0;
        }
        mCurChapter = mCurChapters.get(mCurPlayPos);
        mCurPlayProgress = 0;
        if (mCurChapter != null) {
            mCurPlayDuration = mCurChapter.mDuration;
        } else {
            mCurPlayDuration = 0;
        }
        RecentImpl.getInstance().saveRecent(RecentBean.CreateRecent(getCurrentBook(), getCurrentChapter(), mCurPlayProgress, System.currentTimeMillis()));

        return playShowTips(mCurBook, mCurChapter, 0);
    }

    private boolean looperPre() {
        if (mCurPlayPos <= mCurChapters.size() - 1 && mCurPlayPos > 0) {
            mCurPlayPos--;
        } else {
            mCurPlayPos = mCurChapters.size() - 1;
        }
        mCurChapter = mCurChapters.get(mCurPlayPos);
        mCurPlayProgress = 0;
        if (mCurChapter != null) {
            mCurPlayDuration = mCurChapter.mDuration;
        } else {
            mCurPlayDuration = 0;
        }
        return playShowTips(mCurBook, mCurChapter, 0);
    }

    private boolean playShowTips(BookBean book, ChapterBean chapter, int startPos) {
        if (book == null || chapter == null){
            return false;
        }

        mCurBook = book;
        mCurChapter = chapter;
        if (TextUtils.isEmpty(mCurChapter.mArtist)) {
            mCurChapter.mArtist = mCurBook.mArtist;
        }

        int mDuration = mCurChapter.mDuration;
        //跳过片头 总时长 以服务器传进为准，来判断跳过音长小于总时长  如果服务没有传Duration这不好使
        if (startPos == 0) {
            if (mDuration != 0 && startPos > mDuration){
                startPos = 0;
            }
        }
        setDownFilePath(chapter);

        PlayProxy playProxy = getPlayProxy();
        if (playProxy == null) {
            return false;
        }
        mCurPlayDuration = mCurChapter.mDuration;
        mCurPlayProgress = startPos;

        RecentImpl.getInstance().saveRecent(RecentBean.CreateRecent(getCurrentBook(), getCurrentChapter(), mCurPlayProgress, System.currentTimeMillis()));


        AudioInfo audioInfo = new AudioInfo(book.mBookId, chapter.mRid, chapter.mName, chapter.mArtist, book.mImgUrl, chapter.md5, chapter.resUrl);
        if(!TextUtils.isEmpty(mCurChapter.mFilePath)){
            audioInfo.mFilePath = mCurChapter.mFilePath;
        }
        isPlayed = true;
        playProxy.play(audioInfo, startPos);
        MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.onPlay();
            }
        });
        prefetchNext();
        return true;
    }

    private final PlayDelegate playDelegate = new PlayDelegate() {
        @Override
        public void PlayDelegate_PreStart(final boolean buffering) {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.onPreStart(buffering);
                }
            });
        }

        @Override
        public void PlayDelegate_RealStart(long realTime) {
            mTryTime = 0;

            setSpeed(mCurSpeed);

            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.onRealPlay();
                }
            });
        }

        @Override
        public void PlayDelegate_Pause() {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID,new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.onPause();
                }
            });
        }

        @Override
        public void PlayDelegate_Continue() {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID,new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.onContinue();
                }
            });
        }

        @Override
        public void PlayDelegate_Failed(final ErrorCode error) {
            if (error != ErrorCode.DECODE_FAILE && error != ErrorCode.NO_DECODER
                    && error != ErrorCode.UNKNOWN && error != ErrorCode.IO_ERROR) {
                // 仅在解码错误和未知错误时需要重试
                mTryTime = MAX_TRY_TIMES;
            }
            if (mTryTime < MAX_TRY_TIMES) {
                mTryTime++;
                PlayProxy playProxy = getPlayProxy();
                if (playProxy != null) {
                    playProxy.play(new AudioInfo(mCurBook.mBookId, mCurChapter.mRid, mCurChapter.mName, mCurChapter.mArtist, mCurChapter.mImgUrl, mCurChapter.md5, mCurChapter.resUrl), mCurPlayProgress);
                }
                return;
            }

            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.onFailed(error.ordinal(), error.name());
                }
            });
            stop();
        }

        @Override
        public void PlayDelegate_Stop(final boolean end, String savePath, int playType) {

            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.onStop(end);
                }
            });
            if (end) {
                playNext(true);
            }
        }

        @Override
        public void PlayDelegate_SeekSuccess(final int pos, int playType) {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.seekSuccess(pos);
                }
            });
        }

        @Override
        public void PlayDelegate_PlayProgress(int total, int playPos, int bufferPos) {
            mCurPlayDuration = total;
            mCurPlayProgress = playPos;
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.playProgress(playPos, total);
                }
            });
        }

        //下面两个回调暂时不发
        @Override
        public void PlayDelegate_WaitForBuffering() {

        }

        @Override
        public void PlayDelegate_WaitForBufferingFinish() {

        }

        @Override
        public void PlayDelegate_CacheProgress(int currentProgress, int fileLenTotal) {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.cacheProgress(currentProgress, fileLenTotal);
                }
            });
        }

        @Override
        public void PlayDelegate_DownloadFinished(String savePath, long id) {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.cacheFinished(savePath, id);
                }
            });
        }

        @Override
        public void PlayDelegate_SetVolume(final int vol) {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID,new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.volumeChanged(vol);
                }
            });
        }

        @Override
        public void PlayDelegate_SetMute(final boolean mute) {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID,new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.muteChanged(mute);
                }
            });
        }

        @Override
        public void PlayDelegate_OnRestart() {
        }

        @Override
        public void PlayDelegate_onFFTDataReceive(final float[] leftFFT, final float[] rightFFT) {
            MessageManager.getInstance().syncNotify(IPlayObserver.EVENT_ID,new MessageManager.Caller<IPlayObserver>() {
                @Override
                public void call() {
                    ob.FFTDataReceive(leftFFT,rightFFT);
                }
            });

        }
    };


}
