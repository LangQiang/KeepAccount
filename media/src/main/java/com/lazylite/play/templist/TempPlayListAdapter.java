package com.lazylite.play.templist;

import android.graphics.ColorFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.godq.media_api.media.IPlayController;
import com.godq.media_api.media.PlayStatus;
import com.lazylite.media.R;
import com.lazylite.mod.App;
import com.godq.media_api.media.bean.ChapterBean;

import java.util.List;

/**
 * @author DongJr
 * @date 2020/3/11
 */
public class TempPlayListAdapter extends BaseQuickAdapter<ChapterBean, BaseViewHolder> {

    private boolean mIsSkin;
    private IPlayController mPlayController;

    public TempPlayListAdapter(@Nullable List<ChapterBean> data, boolean isSkin, @NonNull IPlayController playController) {
        super(R.layout.layout_temp_play_item, data);
        this.mIsSkin = isSkin;
        this.mPlayController = playController;
    }

    @Override
    protected void convert(BaseViewHolder helper, ChapterBean item) {
        //播放状态
        LottieAnimationView playingView = helper.getView(R.id.playing_state);
        TextView tvTitle = helper.getView(R.id.tv_title);
        ChapterBean curChapter = mPlayController.getCurrentChapter();
        if (curChapter != null && curChapter.mRid == item.mRid){
            playingView.setVisibility(View.VISIBLE);
            setLottieColor(playingView);
            tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.app_theme_color));
            tvTitle.getPaint().setFakeBoldText(true);
            if (mPlayController.getPlayStatus() == PlayStatus.STATUS_PLAYING){
                playAnimation(playingView);
            } else {
                cancelAnimation(playingView);
            }
        } else {
            playingView.setVisibility(View.GONE);
            if (mIsSkin){
                tvTitle.setTextColor(App.getInstance().getResources().getColor(R.color.LRLiteBase_cl_white_alpha_80));
            } else {
                tvTitle.setTextColor(App.getInstance().getResources().getColor(R.color.LRLiteBase_cl_black_alpha_80));
            }
            tvTitle.getPaint().setFakeBoldText(false);
            if (playingView.isAnimating()){
                playingView.cancelAnimation();
            }
        }
        //付费标
        ImageView imageView = helper.getView(R.id.iv_can_play);
        if (mIsSkin){
            imageView.setImageResource(R.drawable.icon_program_list_download_money_white);
        } else {
            imageView.setImageResource(R.drawable.icon_program_list_download_money);
        }
        tvTitle.setText(item.mName);
//        imageView.setVisibility(item.canplay ? View.GONE : View.VISIBLE);
        imageView.setVisibility(View.GONE);

        if (mIsSkin) {
            helper.setBackgroundColor(R.id.temp_play_list_divider, App.getInstance().getResources().getColor(R.color.white6));
        } else {
            helper.setBackgroundColor(R.id.temp_play_list_divider, App.getInstance().getResources().getColor(R.color.black6));
        }
    }

    private void setLottieColor(LottieAnimationView lottieAnimationView) {
        ColorFilter filter = new SimpleColorFilter(ContextCompat.getColor(mContext, R.color.app_theme_color));
        lottieAnimationView.addValueCallback(new KeyPath("**"), LottieProperty.COLOR_FILTER, new LottieValueCallback<>(filter));
    }

    private void playAnimation(LottieAnimationView playingView){
        if (!playingView.isAnimating()){
            playingView.playAnimation();
        }
    }

    private void cancelAnimation(LottieAnimationView playingView){
        if (playingView.isAnimating()){
            playingView.cancelAnimation();
        }
    }
}
