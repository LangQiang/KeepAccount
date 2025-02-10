package com.lazylite.play.playpage;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.godq.media_api.media.IPlayController;
import com.godq.media_api.media.IPlayObserver;
import com.godq.media_api.media.PlayStatus;
import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;
import com.lazylite.media.R;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.mod.App;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.utils.psrc.PsrcInfo;
import com.lazylite.mod.utils.psrc.PsrcOptional;
import com.lazylite.mod.utils.toast.KwToast;
import com.lazylite.play.PlayPageContract;
import com.lazylite.play.templist.TempPlayListDialog;
import com.lazylite.play.timing.TimingDialog;

/**
 * @author yjsf216
 * 2018/5/23
 */
public class PlayPagePresenter implements PlayPageContract.IPresenter {

    private PlayPageContract.IView mIView;

    private IPlayController playController;

    private PsrcInfo mPsrcInfo;

    public PlayPagePresenter() {
        playController = PlayControllerImpl.getInstance();
    }

    @Override
    public void init(@NonNull PlayPageContract.IView iView) {
        mIView = iView;
        onStart();
    }


    private IPlayObserver playControlObserver = new IPlayObserver() {
        @Override
        public void onPlay() {
            if (isValid()) {
                mIView.setKeepScreenOn(true);
                initAllView();
                mIView.showLoading(true);
            }
        }

        @Override
        public void onPreStart(boolean isLocal) {

        }

        @Override
        public void onRealPlay() {
            if (isValid()) {
                mIView.refreshView(playController.getCurrentBook(), getChapter());
                mIView.refreshPlayStateBtn(false, playController.getPlayStatus());
                mIView.showLoading(false);
                mIView.onTimerState(true);
            }
        }

        @Override
        public void cacheProgress(int currentProgress, int total) {

        }

        @Override
        public void cacheFinished(String savePath, long id) {

        }

        @Override
        public void playProgress(int playPos, int total) {


        }

        @Override
        public void seekSuccess(int pos) {

        }

        @Override
        public void onStop(boolean end) {
            if (isValid()) {
                mIView.refreshPlayStateBtn(end, playController.getPlayStatus());
                mIView.showLoading(false);
                mIView.onTimerState(false);
            }
        }

        @Override
        public void onFailed(int errCode, String msg) {
            MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
                @Override
                public void call() {
                    if (isValid()) {
                        if (playController.getPlayStatus() != PlayStatus.STATUS_BUFFERING &&
                                playController.getPlayStatus() != PlayStatus.STATUS_PLAYING) {
                            initAllView();
                            mIView.showLoading(false);
                            if (!NetworkStateUtil.isAvailable()) {
                                KwToast.show(App.getInstance().getString(R.string.base_net_nocontent_tip));
                            } else {
                                KwToast.show(App.getInstance().getString(R.string.lrlite_media_playfail));
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onContinue() {
            if (isValid()) {

                mIView.refreshPlayStateBtn(false, playController.getPlayStatus());
                mIView.setKeepScreenOn(true);
                mIView.onTimerState(true);
            }
        }

        @Override
        public void onPause() {
            if (isValid()) {
                mIView.refreshPlayStateBtn(false, playController.getPlayStatus());
                mIView.setKeepScreenOn(false);
                mIView.showLoading(false);
                mIView.onTimerState(false);
            }
        }

        @Override
        public void FFTDataReceive(float[] leftFFT, float[] rightFFT) {

        }

        @Override
        public void volumeChanged(int vol) {

        }

        @Override
        public void muteChanged(boolean mute) {

        }

        @Override
        public void speedChanged(float mCurSpeed) {
            if (isValid()) {
                mIView.setMultipleBtnText(playController.getSpeed());
            }
        }

        @Override
        public void playModeChanged(int mCurPlayMode) {

        }
    };


    /**
     * 切换歌曲的初始化
     */
    private void initAllView() {
        if (isValid()) {
            mIView.refreshView(playController.getCurrentBook(), playController.getCurrentChapter());
            mIView.refreshPlayStateBtn(false, playController.getPlayStatus());
        }
    }


    public void onStart() {
        MessageManager.getInstance().attachMessage(IPlayObserver.EVENT_ID, playControlObserver);
        mPsrcInfo = PsrcOptional.buildRoot("播放页", -1);

        initAllView();
        if (isValid()) {
            //倍数图标初始化
            mIView.setMultipleBtnText(playController.getSpeed());
            mIView.setTimingCallback();
        }
    }


    @Override
    public void play() {
        if (!isValid()) {
            return;
        }
        if (playController.getPlayStatus() == PlayStatus.STATUS_PLAYING) {
            pause();
        } else {
            playController.continuePlay();
        }
    }

    private void pause() {
        if (playController != null) {
            playController.pause();
        }
    }

    @Override
    public void playNext() {
        if (playController != null) {
            playController.playNext(false);
        }
    }

    @Override
    public void playPre() {
        if (playController != null) {
            playController.playPre(false);
        }
    }


    @Override
    public void share() {

//        if (mIModel != null && mIModel.getBook() != null) {
//            final BookBean book = mIModel.getBook();
//            String targetUrl = TsWebUrlManager.getH5BookInfoUrl(String.valueOf(book.mBookId));
//            String msgweibo = "分享网页：" + book.mTitle + "," + book.mTitle + "(@懒人极速版)";
//            String msgqqzone = book.mTitle;
//            String msgwxtitle = book.mTitle;
//            String msgwxtext = book.mTitle;
//            String imageUrl = book.mImgUrl;
//            if (imageUrl == null) {
//                imageUrl = "";
//            }
//            if (!imageUrl.startsWith("http:") && !imageUrl.startsWith("https:")) {
//                imageUrl = "http:" + imageUrl;
//            }
//            ShareMsgInfo shareMsgInfo = new ShareMsgInfo(msgwxtitle, msgweibo, targetUrl, "", msgweibo, msgwxtext,
//                    msgqqzone);
//            shareMsgInfo.setFrom(OnlineFragment.FROM_WEB);
//            shareMsgInfo.setImageUrl(imageUrl);
//            ShareUtils.getInstance().shareMsgInfo(shareMsgInfo, false, ShareUtils.TYPE_BG_UI_BLUR, new OnShareListener() {
//                @Override
//                public void onFinish(int type) {
//                    Share.sendShareLog(type, -1, book.mBookId, book.mTitle, mPsrcInfo);
//                }
//            });
//        }
    }


    @Override
    public void clickTime(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        TimingDialog timingDialog = new TimingDialog(activity);
        timingDialog.showBlurBg(true);
        timingDialog.show();
    }


    @Override
    public void clickList(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        TempPlayListDialog.pop(activity, true);
    }


    @Override
    public void jumpToAnchorDetail(String from) {

    }

    @Override
    public void adjustment(boolean isPlus, int m) {
        int mDuration = playController.getDuration();
        int mCurrentPosition = playController.getCurrentProgress();
        if (mDuration > 0) {
            if (isPlus) {
                //快进m毫秒
                mCurrentPosition = mCurrentPosition + m;
            } else {
                //快退m毫秒
                mCurrentPosition = mCurrentPosition - m;
            }

            if (mCurrentPosition > mDuration - 2) {
                mCurrentPosition = mDuration - 2;
            } else if (mCurrentPosition < 2) {
                mCurrentPosition = 0;
            }

            sendSeekEvent(mCurrentPosition);
        }
    }


    @Override
    public void initData() {
    }

    @Override
    public BookBean getBook() {
        return null;
    }

    @Override
    public ChapterBean getChapter() {
        return null;
    }


    private void sendSeekEvent(final int pos) {
        if (playController == null) {
            return;
        }
        playController.seekTo(pos);
    }


    private boolean isValid() {
        if (mIView == null || playController == null) {
            return false;
        }
        return true;
    }

    @Override
    public void detachMV() {
        if (isValid()) {
            mIView.removeTimingCallback();
        }
        MessageManager.getInstance().detachMessage(IPlayObserver.EVENT_ID, playControlObserver);
    }
}
