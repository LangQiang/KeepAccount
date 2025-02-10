package com.godq.media_api.media;


import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;

import java.util.List;

public interface IPlayController {

    boolean play(BookBean bookBean, List<? extends ChapterBean> chapters, int index, int startPos);

    boolean playNext(boolean isAuto);

    boolean playPre(boolean isAuto);

    void pause();

    boolean continuePlay();

    void stop();

    void seekTo(int pos);

    int getCachePos();

    void setPlayMode(int playMode);

    int getPlayMode();

    void setSpeed(float speed);

    float getSpeed();

    int getChaptersPlayIndex();

    @PlayStatus int getPlayStatus();

    int getDuration();

    int getCurrentProgress();

    int getPreparePercent();

    List<ChapterBean> getPlayList();

    BookBean getCurrentBook();

    ChapterBean getCurrentChapter();

    void updateChapterList(List<? extends ChapterBean> chapterBeans);

    void clearPlayCache(long albumId, long chapterId);

    void clearPlayList();

    boolean removeOne(long chapterId);
}
