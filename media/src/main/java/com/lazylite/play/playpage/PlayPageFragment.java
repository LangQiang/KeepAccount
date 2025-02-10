package com.lazylite.play.playpage;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import com.facebook.drawee.view.SimpleDraweeView;
import com.godq.media_api.media.IPlayController;
import com.godq.media_api.media.PlayStatus;
import com.lazylite.media.R;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.mod.App;
import com.godq.media_api.media.bean.BookBean;
import com.godq.media_api.media.bean.ChapterBean;
import com.lazylite.mod.receiver.network.NetworkStateUtil;
import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.utils.KwSystemSettingUtils;
import com.lazylite.mod.utils.ScreenUtility;
import com.lazylite.mod.utils.ViewChangeAnimationUtils;
import com.lazylite.mod.utils.toast.KwToast;
import com.lazylite.mod.widget.SquareRoundViewGroup;
import com.lazylite.mod.widget.swipeback.SwipeBackLayout;
import com.lazylite.mod.widget.textview.IconView;
import com.lazylite.play.BasePlayFragment;
import com.lazylite.play.PlayHelper;
import com.lazylite.play.PlayPageContract;
import com.lazylite.play.playpage.widget.PaletteBlurBgView;
import com.lazylite.play.playpage.widget.PlaySpeedDialog;
import com.lazylite.play.playpage.widget.SeekBarDelegate;
import com.lazylite.play.playpage.widget.newseekbar.PlaySeekBar;
import com.lazylite.play.timing.TsSleepCtr;


public class PlayPageFragment extends BasePlayFragment implements View.OnClickListener, PlayPageContract.IView,
        SeekBarDelegate.OnSeekListener {

    private PlaySeekBar playSeekBar;
    private IPlayController playController;
    private PlaySpeedDialog mPlaySpeedDialog;
    private PlayPagePresenter mPresenter;
    private SimpleDraweeView ivCover;
    private ImageView loadingView;
    private PaletteBlurBgView paletteBlurBgView;
    private IconView playingTimingIcon, ivSpeed;
    private TextView tvCurrentTime, tvTotalTime, playingTimingTv, tvChapterTitle, tvAnchor, playBtn;
    private View mRootView;
    private boolean isShowLoading = false;
    private ValueAnimator animator = null;
    private SeekBarDelegate mSeekBarDelegate;
    private String mDuration = "00";
    private String from;
    private LinearLayout llPlayingTime;
    private IconView playTimeForward;
    private LinearLayout llSpeed;
    private IconView playingNext;
    private IconView playingPre;
    private LinearLayout llPlayList;
    private IconView playTimeBack, ivBack;
    private ViewGroup rootView;


    public static PlayPageFragment newInstance(String from) {
        PlayPageFragment playPageFragment = new PlayPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("from", from);
        playPageFragment.setArguments(bundle);
        return playPageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playController = PlayControllerImpl.getInstance();

//        psrcInfo = PsrcOptional.checkAndAddName(psrcInfo, "播放页");
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lrlite_media_fragment_play, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwipeBackLayout swipView = getSwipeBackLayout();
        if (swipView != null) {
            swipView.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
            swipView.setEdgeSize(DeviceInfo.HEIGHT);
            swipView.setScrollThresHold(0.1f);
        }
        initView(view);
        initData();
        if (!NetworkStateUtil.isAvailable()) {
            KwToast.show(getString(R.string.base_net_nocontent_tip));
        }

    }



    private void initView(View view) {
        mRootView = view;
        playSeekBar = view.findViewById(R.id.music_seekbar);
        ivCover = view.findViewById(R.id.iv_cover);
        paletteBlurBgView = view.findViewById(R.id.palette_view);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        playingTimingTv = view.findViewById(R.id.playing_timing_tv);
        playingTimingIcon = view.findViewById(R.id.playing_timing_icon);
        tvChapterTitle = view.findViewById(R.id.tv_chapter_title);
        tvAnchor = view.findViewById(R.id.tv_anchor_blog);
        ivSpeed = view.findViewById(R.id.iv_speed);
        playBtn = view.findViewById(R.id.playing_play);
        loadingView = view.findViewById(R.id.playing_loading);

        llPlayingTime = view.findViewById(R.id.ll_playing_time);
        llPlayingTime.setOnClickListener(this);
        llPlayList = view.findViewById(R.id.ll_play_list);
        llPlayList.setOnClickListener(this);
        playingPre = view.findViewById(R.id.playing_pre);
        playingPre.setOnClickListener(this);
        playingNext = view.findViewById(R.id.playing_next);
        playingNext.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        llSpeed = view.findViewById(R.id.ll_speed);
        llSpeed.setOnClickListener(this);
        playTimeForward = view.findViewById(R.id.play_time_forward_btn);
        playTimeForward.setOnClickListener(this);
        playTimeBack = view.findViewById(R.id.play_time_back_btn);
        playTimeBack.setOnClickListener(this);
        ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        tvAnchor.setOnClickListener(this);
        seekBarSetting();
        coverSetting(view);
        KwSystemSettingUtils.resetStatusBarWhite(App.getMainActivity());

        SwipeBackLayout swipView = getSwipeBackLayout();
        if (swipView != null) {
            swipView.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_TOP);
            swipView.setEdgeSize(DeviceInfo.HEIGHT);
            swipView.setScrollThresHold(0.1f);
        }

        if (!NetworkStateUtil.isAvailable()) {
            KwToast.show(getString(R.string.base_net_nocontent_tip));
        }
    }


    private void coverSetting(View view) {
        if (DeviceInfo.HEIGHT <= 1920 && getActivity() != null) {
            Guideline guidelineCoverTop = view.findViewById(R.id.guide_cover_top);
            SquareRoundViewGroup srgCover = view.findViewById(R.id.srg_cover);
            guidelineCoverTop.setGuidelinePercent(0.1f);
            boolean hasNavigationBar = ScreenUtility.checkHasNavigationBar(getActivity());
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) srgCover.getLayoutParams();
            layoutParams.leftMargin = ScreenUtility.dip2px(hasNavigationBar ? 67 : 62);
            layoutParams.rightMargin = ScreenUtility.dip2px(hasNavigationBar ? 67 : 62);
            srgCover.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void Resume() {
        super.Resume();
        KwSystemSettingUtils.resetStatusBarWhite(getActivity());
    }

    private void initData() {
        mPresenter = new PlayPagePresenter();
        mPresenter.init(this);
        mPresenter.initData();
        if (getArguments() != null) {
            from = getArguments().getString("from");
        }
    }


    @Override
    public void refreshPlayStateBtn(boolean end, int playStatus) {
        if (playStatus == PlayStatus.STATUS_PLAYING) {
            playBtn.setText(getString(R.string.icon_play_page_pause));
            playBtn.setContentDescription("暂停");
        } else {
            playBtn.setText(getString(R.string.icon_play_page_play));
            playBtn.setContentDescription("播放");
        }
        if (end && playSeekBar != null) {
            playSeekBar.setCurrent(100);
            tvCurrentTime.setText(mDuration);
        }
    }

    @Override
    public void onTimerState(boolean start) {
        if (mSeekBarDelegate == null) return;
        if (start) {
            mSeekBarDelegate.startTimer();
        } else {
            mSeekBarDelegate.stopTimer();
        }
    }


    @Override
    public void showLoading(boolean show) {
        if (isShowLoading == show) {
            return;
        }
        isShowLoading = show;
        if (show) {
            endLastAnimatorIfNeeded();
            animator = ViewChangeAnimationUtils.animationSwitch(loadingView, playBtn, false);
        } else {
            endLastAnimatorIfNeeded();
            animator = ViewChangeAnimationUtils.animationSwitch(playBtn, loadingView, true);
        }
    }


    @Override
    public void setMultipleBtnText(float speed) {
        if (ivSpeed != null) {
            if (speed == 0.5f) {
                ivSpeed.setText(getString(R.string.icon_play_page_seep_0_5));
            } else if (speed == 0.75f) {
                ivSpeed.setText(getString(R.string.icon_play_page_seep_0_75));
            } else if (speed == 1f) {
                ivSpeed.setText(getString(R.string.icon_play_page_seep_1_0));
            } else if (speed == 1.25f) {
                ivSpeed.setText(getString(R.string.icon_play_page_seep_1_25));
            } else if (speed == 1.5f) {
                ivSpeed.setText(getString(R.string.icon_play_page_seep_1_5));
            } else if (speed == 2.0f) {
                ivSpeed.setText(getString(R.string.icon_play_page_seep_2_0));
            }
        }
    }

    @Override
    public void setTimingCallback() {
        TsSleepCtr.CallBack callBack = new TsSleepCtr.CallBack() {
            @Override
            public void resetView() {
                if (playingTimingIcon == null || playingTimingTv == null) {
                    return;
                }
                playingTimingTv.setText("开启定时");
                playingTimingIcon.setText(getString(R.string.icon_play_page_timing));
            }

            @Override
            public void updateTime(boolean isFirst) {
                if (playingTimingIcon == null || playingTimingTv == null) {
                    return;
                }
                if (isFirst) {
                    playingTimingIcon.setText(getString(R.string.icon_timing_close));
                }
                String timeStr = TsSleepCtr.getIns().getTimeStr();
                playingTimingTv.setText(timeStr);
            }

            @Override
            public void updateChp(boolean isFirst) {
                if (playingTimingIcon == null || playingTimingTv == null) {
                    return;
                }
                if (isFirst) {
                    playingTimingIcon.setText(getString(R.string.icon_timing_close));
                }
                String nemStr = TsSleepCtr.getIns().getNemStr();
                playingTimingTv.setText(nemStr);
            }
        };
        if (TsSleepCtr.getIns().isNumMode()) {
            callBack.updateChp(true);
        } else if (TsSleepCtr.getIns().isTimeMode()) {
            callBack.updateTime(true);
        } else {
            callBack.resetView();
        }
        TsSleepCtr.getIns().addCallBack(2, callBack);
    }

    @Override
    public void removeTimingCallback() {
        TsSleepCtr.getIns().removeCallBack(2);
    }


    @Override
    public void setKeepScreenOn(boolean on) {
        if (mRootView != null) {
            mRootView.setKeepScreenOn(on);
        }
    }

    @Override
    public void refreshView(BookBean bookBean, ChapterBean chapterBean) {
        setBlurBackground(paletteBlurBgView, ivCover);
        if (bookBean == null || chapterBean == null) return;
        tvChapterTitle.setText(chapterBean.mName);
        tvAnchor.setVisibility(View.VISIBLE);
        tvAnchor.setText(bookBean.mName);
        playSeekBar.setData(chapterBean.mRid);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_speed) {
            if (mPlaySpeedDialog == null) {
                mPlaySpeedDialog = new PlaySpeedDialog(getActivity());
            }
            mPlaySpeedDialog.showDialog();
        } else if (id == R.id.playing_play) {
            if (playController == null) {
                return;
            }
            if (playController.getPlayStatus() == PlayStatus.STATUS_PLAYING) {
                playController.pause();
            } else {
                playController.continuePlay();
            }
        } else if (id == R.id.playing_next) {
            if (mPresenter != null) {
                mPresenter.playNext();
            }
        } else if (id == R.id.playing_pre) {
            if (mPresenter != null) {
                mPresenter.playPre();
            }
        } else if (id == R.id.tv_anchor_blog) {
            if (mPresenter != null) {
                mPresenter.jumpToAnchorDetail(from);
            }
        } else if (id == R.id.ll_play_list) {
            if (mPresenter != null) {
                mPresenter.clickList(getActivity());
            }
        } else if (id == R.id.ll_playing_time) {
            if (mPresenter != null) {
                mPresenter.clickTime(getActivity());
            }
        } else if (id == R.id.play_time_forward_btn) {
            if (mPresenter != null) {
                mPresenter.adjustment(true, 15000);
            }
        } else if (id == R.id.play_time_back_btn) {
            if (mPresenter != null) {
                mPresenter.adjustment(false, 15000);
            }
        } else if (id == R.id.iv_back) {
            close();
        }
//        else if (id == R.id.iv_share) {
//            if (!NetworkStateUtil.isAvailable()) {
//                KwToast.show(getString(R.string.base_net_nocontent_tip));
//                return;
//            }
//            KwToast.show("分享");
//        }
    }


    private void seekBarSetting() {
        mSeekBarDelegate = new SeekBarDelegate();
        mSeekBarDelegate.attach(playSeekBar, this);
        mSeekBarDelegate.setLifecycleOwner(this);
    }

    @Override
    public void onPlaySeekBarChangeByTimer(String currentTime, String duration, int progress, int cacheProgress) {
        if (playSeekBar != null) {
            playSeekBar.setCurrent(progress);
            playSeekBar.setCurrentCache(cacheProgress);
        }
        setTotalTime(duration);
        tvCurrentTime.setText(currentTime);
    }

    @Override
    public void onPlaySeekBarChangeByUser(String currentTime, String duration) {
        tvTotalTime.setText(duration);
        setTotalTime(duration);
        tvCurrentTime.setText(currentTime);
    }


    private void endLastAnimatorIfNeeded() {
        if (animator != null && animator.isRunning()) {
            animator.end();
        }
    }

    private void setTotalTime(String totalTime) {
        if (!TextUtils.isEmpty(totalTime) && !TextUtils.isEmpty(mDuration) &&
                !totalTime.equals(mDuration) && tvTotalTime != null) {
            tvTotalTime.setText(totalTime);
            mDuration = totalTime;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachMV();
        }
        endLastAnimatorIfNeeded();
    }

    public boolean isNeedSwipeBack() {
        return true;
    }
}
