package com.lazylite.play.templist;

import androidx.annotation.NonNull;

import com.godq.media_api.media.IPlayObserver;
import com.godq.media_api.media.IMediaService;
import com.godq.media_api.media.IPlayController;
import com.lazylite.bridge.router.ServiceImpl;
import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;
import com.lazylite.mod.http.mgr.IKwHttpFetcher;
import com.lazylite.mod.http.mgr.KwHttpMgr;
import com.lazylite.mod.http.mgr.model.IResponseInfo;
import com.lazylite.mod.http.mgr.model.RequestInfo;
import com.lazylite.mod.messagemgr.MessageManager;

import java.util.List;

/**
 * @author DongJr
 * @date 2020/3/11
 */
public class TempPlayListManager {

    private IPlayController playController;

    private boolean isInit;

    private TempPlayListManager() {
        IMediaService service = (IMediaService) ServiceImpl.getInstance().getService(IMediaService.class.getName());
        if (service != null) {
            playController = service.getPlayController();
        }
    }

    public static TempPlayListManager getInstance(){
        return SingleTon.instance;
    }

    private static class SingleTon{
        static TempPlayListManager instance = new TempPlayListManager();
    }

    private final IPlayObserver mPlayObserver = new TempListPlayObserver();

    public void init(){
        if (!isInit) {
            isInit = true;
            MessageManager.getInstance().attachMessage(IPlayObserver.EVENT_ID, mPlayObserver);
        }
    }

    /**
     * 向下加载更多
     */
    public void loadMoreData(OnLoadDataListener listener){
        if (isAvailable()){
            BookBean curBook = playController.getCurrentBook();
            List<ChapterBean> list = playController.getPlayList();
//            String url = UrlManagerUtils.getNearbyProgramList(curBook.mBookId, list.get(list.size() - 1).mRid,
//                    -1, 50, curBook.listStatus, curBook.sortType);
//            loadAndCheckChapterList(curBook, url, listener);
        }
    }

    /**
     * 向上加载更多
     */
    public void refreshMoreData(OnLoadDataListener listener){
        if (isAvailable()){
            BookBean curBook = playController.getCurrentBook();
            List<ChapterBean> list = playController.getPlayList();
//            String url = UrlManagerUtils.getNearbyProgramList(curBook.mBookId, list.get(0).mRid,
//                    50, -1, curBook.listStatus, curBook.sortType);
//            loadAndCheckChapterList(curBook, url, listener);
        }
    }

    void autoLoadPlayList(){
        if (!isAvailable()){
            return;
        }
        BookBean curBook = playController.getCurrentBook();
        List<ChapterBean> list = playController.getPlayList();
        int curPosition = playController.getChaptersPlayIndex();
        if ((curBook.mCount == 0 || list.size() < curBook.mCount) && list.size() - curPosition < 3){
            loadMoreData(new OnLoadDataListener() {
                @Override
                public void onLoadSuccess(ChapterList list) {
                    if (playController != null) {
                        List<ChapterBean> tsNowPlaylist = playController.getPlayList();
                        if (tsNowPlaylist != null) {
                            tsNowPlaylist.addAll(list);
                        }
                    }
                }

                @Override
                public void onLoadFailed() {
//                    PlayStopLogger.send(new PlayStopLogger.PlayStopInfo(Constants.STOP_TYPE_PLAY, Constants.PLAY_REASON_LOAD_LIST_FAIL));
                }
            });
        }
    }

    private void loadAndCheckChapterList(final BookBean bookBean, String url, final OnLoadDataListener listener){
        KwHttpMgr.getInstance().getKwHttpFetch().asyncGet(RequestInfo.newGet(url), new IKwHttpFetcher.FetchCallback() {
            @Override
            public void onFetch(@NonNull IResponseInfo responseInfo) {
                if (responseInfo.isSuccessful()) {
                    String data = responseInfo.dataToString();
                    try {
                        ChapterList list = TempPlayListParser.parse(data);
                        if (list.getBook().mCount != 0) {
                            bookBean.mCount = list.getBook().mCount;
                        }
                        if (list.isEmpty()) {
                            if (listener != null) {
                                listener.onLoadSuccess(list);
                            }
                        } else {
                            checkChapterPayInfo(bookBean, list, listener);
                        }
                    } catch (Exception e) {
                        if (listener != null) {
                            listener.onLoadSuccess(new ChapterList());
                        }
                    }
                } else {
                    if (listener != null){
                        listener.onLoadFailed();
                    }
                }
            }
        });
    }


    private void checkChapterPayInfo(BookBean bookBean, final ChapterList list, final OnLoadDataListener listener){
        listener.onLoadSuccess(list);
    }


    private boolean isAvailable(){
        if (playController == null) {
            return false;
        }
        BookBean curBook = playController.getCurrentBook();
        if (curBook == null){
            return false;
        }
        List<ChapterBean> list = playController.getPlayList();
        return list != null && !list.isEmpty();
    }

 }
