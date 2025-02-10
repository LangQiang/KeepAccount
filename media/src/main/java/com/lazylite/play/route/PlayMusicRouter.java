package com.lazylite.play.route;

import android.net.Uri;

import com.godq.deeplink.route.AbsRouter;
import com.lazylite.annotationlib.DeepLink;

@DeepLink(path = "/music")
public class PlayMusicRouter extends AbsRouter {

    private int isOpenPlayPage;

    private int startPos;

    private int sortBy;

    private String rid;

    private String albumId;



//    private ChapterList chapterBeans;



    @Override
    protected void parse(Uri uri) {

        Runtime.getRuntime().gc();
        this.originUri = uri;

        this.isOpenPlayPage = toInt(uri.getQueryParameter("openPlayPage"), 0);

        this.startPos = toInt(uri.getQueryParameter("startPos"), 0);

        this.sortBy = toInt(uri.getQueryParameter("sortby"), 3);

        this.rid = uri.getQueryParameter("musicId");

        this.albumId = uri.getQueryParameter("albumId");
    }

    @Override
    public boolean route() {
        if (!isRunning) {
            return false;
        }
//        if (chapterBeans == null || chapterBeans.size() == 0) {
//            isRunning = false;
//            return false;
//        }
//        ChapterBean curChapter = ModMgr.getPlayControl().getCurChapter();
//        if (curChapter != null && curChapter.mRid == chapterBeans.get(0).mRid) {
//            if (isOpenPlayPage != 0) {
//                TsJump.jump2CurrentPlayFrg(true);
//            } else {
//                ModMgr.getPlayControl().tingshuContinuePlay();
//            }
//            return true;
//        }
//        TsPlayUtils.play(chapterBeans.getBook(), chapterBeans, 0, startPos, null, new TsPlayUtils.IPlayListener() {
//            @Override
//            public void preparePlay(int behavior) {
//                if (isOpenPlayPage != 0) {
//                    TsJump.jump2CurrentPlayFrg(true);
//                }
//            }
//
//            @Override
//            public void onFail() {
//                isRunning = false;
//            }
//
//            @Override
//            public boolean interceptBeforePlay() {
//                return !isRunning;
//            }
//        });
        return true;
    }

    @Override
    protected boolean hasBackgroundTask() {
        return true;
    }

    @Override
    protected void runInBackground() {
        try {
//            long id = Long.parseLong(rid);
//            String url = LRLiteUrlManagerUtils.getProgramListById(Long.parseLong(albumId),id);
//            HttpSession httpSession = new HttpSession();
//            HttpResult httpResult = httpSession.get(url);
//            if (!httpResult.isOk()) {
//                return;
//            }
//            chapterBeans = ChapterList.parseSingleMusicInfo(httpResult.dataToString());
//            if (chapterBeans != null && chapterBeans.getBook() != null) {
//                chapterBeans.getBook().sortType = sortBy;
//            }
        } catch (Exception e) {
            //
        }
    }


    private int toInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
