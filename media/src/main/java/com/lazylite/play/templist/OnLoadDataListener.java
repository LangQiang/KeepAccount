package com.lazylite.play.templist;

/**
 * @author DongJr
 * @date 2020/3/11
 */
public interface OnLoadDataListener {

    void onLoadSuccess(ChapterList list);

    void onLoadFailed();
}
