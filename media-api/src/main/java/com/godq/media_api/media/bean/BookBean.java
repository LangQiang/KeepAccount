package com.godq.media_api.media.bean;


public class BookBean {

	//排序方式
	public final static int SORT_TYPE_ASC = 1;
	public final static int SORT_TYPE_DESC = 3;
	public final static int SORT_TYPE_UNKNOWN = -1;

	public long mBookId;

	public String mName;

	public String mDescription;

	public String mArtist;

	public int mCount;

	public long mPlayCount; //总的播放量 PlCntAll

	public String mImgUrl;

	public int sortType;

	public void setBook(BookBean book) {
		this.mName = book.mName;
		this.mBookId = book.mBookId;
		this.mArtist = book.mArtist;
		this.mCount = book.mCount;
		this.mDescription = book.mDescription;
		this.mImgUrl = book.mImgUrl;
		this.mPlayCount = book.mPlayCount;
	}

}
