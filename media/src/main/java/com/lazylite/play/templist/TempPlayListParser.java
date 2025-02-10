package com.lazylite.play.templist;

import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;
import com.godq.media_api.media.bean.Parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DongJr
 * @date 2020/3/12
 */
public class TempPlayListParser {

    public static ChapterList parse(String data) throws Exception {
        ChapterList chapterList = new ChapterList();

        JSONObject jsonObject = new JSONObject(data).optJSONObject("data");

        //尽量取下专辑名字和主播名字
        String albumName = "";
        String anchorName = "";
        JSONObject albumObj = jsonObject.optJSONObject("album");
        if (albumObj != null) {
            albumName = albumObj.optString("albumName");
            anchorName = albumObj.optString("anchorName");
        }

        JSONArray jsonArray = jsonObject.optJSONArray("list");
        if (jsonArray == null) {
            return chapterList;
        }
        List<ChapterBean> chapterBeans = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            chapterBeans.add(Parser.parseChapter(jsonArray.optJSONObject(i), albumName, anchorName));
        }
        if (albumObj != null) {
            chapterList.bookID = albumObj.optLong("albumId");
            chapterList.mbookImg = albumObj.optString("cover");
            BookBean bookBean = new BookBean();
            bookBean.mBookId = albumObj.optLong("albumId");
            bookBean.mName = albumObj.optString("albumName");
            bookBean.mArtist = albumObj.optString("anchorId");
            bookBean.mImgUrl = albumObj.optString("cover");
            bookBean.mCount = albumObj.optInt("trackCount");
            bookBean.sortType = BookBean.SORT_TYPE_ASC;

            chapterList.setBook(bookBean);
        }
        chapterList.addAll(chapterBeans);
        return chapterList;
    }

}
