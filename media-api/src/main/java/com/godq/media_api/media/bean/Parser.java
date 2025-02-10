package com.godq.media_api.media.bean;


import androidx.annotation.NonNull;

import org.json.JSONObject;

/**
 * 全局都要用这个类解析服务返回数据 不满足要和服务协调
 *
 * */
public class Parser {

    @NonNull
    public static ChapterBean parseChapter(JSONObject object, String albumName, String anchorName) {

        ChapterBean chapterBean = new ChapterBean();
        chapterBean.mBookId = object.optLong("albumId");
        chapterBean.mBookName = albumName;
        chapterBean.mImgUrl = object.optString("cover");
        chapterBean.mArtist = anchorName;

        chapterBean.mRid = object.optLong("trackId");
        chapterBean.mName = object.optString("trackName");
        chapterBean.mDuration = object.optInt("duration");
        chapterBean.md5 = object.optString("md5");

        return chapterBean;
    }

}
