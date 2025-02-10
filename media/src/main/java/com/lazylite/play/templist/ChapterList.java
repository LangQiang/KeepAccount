package com.lazylite.play.templist;

import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;

import java.util.ArrayList;

public class ChapterList extends ArrayList<ChapterBean> {

	private static final long serialVersionUID = -6936408113571805996L;

    public long bookID;
    public String mbookImg;
    private BookBean mBook;

	public void setBook(BookBean book){
		if (this.mBook != null){
			this.mBook.setBook(book);
		}else {
			this.mBook = book;
		}
	}

	public BookBean getBook(){
		return mBook;
	}

}
