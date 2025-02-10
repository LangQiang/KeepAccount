package com.lazylite.play.recent;

import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;

public class RecentBean {

    private BookBean bookBean;

    private ChapterBean chapterBean;

    private int progress;//进度 单位毫秒

    private long lastAccessTime; //上一次播放的时间

    public static RecentBean CreateRecent(BookBean currentBook, ChapterBean currentChapter, int mCurPlayProgress, long currentTimeMillis) {
        RecentBean recentBean = new RecentBean();
        recentBean.bookBean = currentBook;
        recentBean.chapterBean = currentChapter;
        recentBean.progress = mCurPlayProgress;
        recentBean.lastAccessTime = currentTimeMillis;
        return recentBean;
    }

    public BookBean getBookBean() {
        return bookBean;
    }

    public void setBookBean(BookBean bookBean) {
        this.bookBean = bookBean;
    }

    public ChapterBean getChapterBean() {
        return chapterBean;
    }

    public void setChapterBean(ChapterBean chapterBean) {
        this.chapterBean = chapterBean;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
}
