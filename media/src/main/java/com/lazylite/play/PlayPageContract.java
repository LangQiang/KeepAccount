package com.lazylite.play;

import android.app.Activity;

import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;

/**
 * @author yjsf216
 * @date 2018/5/23
 */
public interface PlayPageContract {

    interface IPresenter {

        /**
         * 解绑IModel和IView
         */
        void detachMV();


        void init(IView iView);

        /**
         * 播放歌曲
         */
        void play();
        /**
         * 播放下一首歌曲
         */
        void playNext();
        /**
         * 播放上一首歌曲
         */
        void playPre();

        /**
         * 点击分享
         */
        void share();

        /**
         * 点击定时
         */
        void clickTime(Activity activity);

        /**
         * 点击列表
         */
        void clickList(Activity activity);

        void jumpToAnchorDetail(String from);

        /**
         * 调节进度
         * @param isPlus true前进 false 后退
         * @param m 毫秒
         */
        void adjustment(boolean isPlus, int m);

        void initData();

        /**
         * 获取当前播放的book信息
         * @return BookBean
         */
        BookBean getBook();
        /**
         * 获取当前播放的章节信息
         * @return ChapterBean
         */
        ChapterBean getChapter();

    }



    interface IView{
        /**
         * 设置倍数图标信息
         */
        void setMultipleBtnText(float speed);

        /**
         * 设置定时监听
         * */
        void setTimingCallback();

        /**
         * 移除定时时长监听
         * */
        void removeTimingCallback();

        /**
         * 缓冲动画
         * @param show 缓冲
         */
        void showLoading(boolean show);

        /**
         * 设置屏幕常亮
         * @param on true为常亮
         */
        void setKeepScreenOn(boolean on);
        /**
         * 章节切换时的刷新 头 和 广告专辑
         */
        void refreshView(BookBean bookBean, ChapterBean chapterBean);

        /**
         * 设置播放按钮状态
         */
        void refreshPlayStateBtn(boolean end,int playStatus);

        void onTimerState(boolean start);

    }
}
