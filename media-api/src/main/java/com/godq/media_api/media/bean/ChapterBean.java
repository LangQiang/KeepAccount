package com.godq.media_api.media.bean;

import android.text.TextUtils;

/**
 * 服务接口一般会在单曲列表里返回专辑id 所以这个可以认为是可靠的  专辑名字和图片会尽量赋值
 */
public class ChapterBean implements IContent {

	public long mBookId;

	public String mName;

	public String mArtist;

	public int mDuration;// 单位秒

	public long mRid;

	public String mFilePath;

	public String mBookName;

	public String mImgUrl;

	public String md5;

	public String resUrl;

	@Override
	public String getName() {
		if (TextUtils.isEmpty(mName)) {
			return "";
		}
		return mName;
	}

	@Override
	public String getInfo() {
		return "";

	}

	@Override
	public String getSonger() {
		if (TextUtils.isEmpty(mArtist)) {
			return "";
		}
		return mArtist;
	}

}
