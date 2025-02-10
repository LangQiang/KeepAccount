package com.lazylite.play.playpage.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * 播放页背景
 *
 * ui的设计必须有三层 醉了。。
 *
 * 华为手机 渲染有问题 把取色分成两部分 比较奇怪
 *
 * */

public class PaletteBlurBgView extends FrameLayout {
    private ImageView blurIv;
    private View palette1;
    private View palette2;

    public PaletteBlurBgView(Context context) {
        super(context);
        init(context);
    }

    public PaletteBlurBgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaletteBlurBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        palette1 = new View(context);
        palette2 = new View(context);
        linearLayout.addView(palette1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        linearLayout.addView(palette2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        addView(linearLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //模糊层 ui要求设置透明度
        blurIv = new ImageView(context);
        blurIv.setAlpha(0.12f);
        blurIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(blurIv, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //最上层遮照
        View topShadowView = new View(context);
        topShadowView.setBackgroundColor(0x1e000000);
        addView(topShadowView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setPaletteBg(int color) {
        palette1.setBackgroundColor(color);
        palette2.setBackgroundColor(color);
    }

    public void setBlurBitmap(Bitmap blurBitmap) {
        if (blurIv != null) {
            blurIv.setImageBitmap(blurBitmap);
        }
    }
}
