package com.lazylite.play.playpage.widget.seekbar;


public interface RangeSeekbarCallBack {

    /**
     * 获取当前seekbar进度
     * @return
     */
    int getCurProgress();

    /**
     * 用户点击或者滑动的区域，是否达到了范围的最小或者最大值
     * @return
     */
    boolean touchMaxOrMin();

}
