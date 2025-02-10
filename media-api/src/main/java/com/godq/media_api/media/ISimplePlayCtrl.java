package com.godq.media_api.media;


public interface ISimplePlayCtrl {
    void setCallback(IPlayCallback callback);

    void play(String path, int pos);

    void setLoopPlay(boolean loopPlay);

    boolean pause();

    boolean resume();

    void stop();

    int getCurrentPos();

    boolean seek(int milsec);

    boolean isPlaying();

    //单位毫秒
    int getDuration();

    void release();

    void setVol(float vol);

    String getDataSource();

    interface IPlayCallback {
        /**
         * 播放器正式开始播放回调函数
         */
        void onStartPlaying(String dataSource);

        /**
         * 缓冲开始回调函数
         */
        void onStartBuffering(String dataSource);

        /**
         * 缓冲结束毁掉函数
         */
        void onEndBuffering(String dataSource);

        /**
         * 播放完成
         */
        void onAutoPlayEnd(String dataSource);

        /**
         * 播放错误
         */
        void onPlayError(String dataSource);
    }

}
