package com.godq.upload.chooseimage;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.godq.upload.R;

/**
 * Created on 16-12-8.
 *
 * @author aprz
 */

public class KSingGalleryTitleBar extends RelativeLayout {
    private TextView mCancelView;
    private TextView mTitleView;
    private ImageView mTitleArrowView;
    private TextView mCountView;
    private TextView mContinueView;
    private View mCountBg;
    private boolean up = false;

    public KSingGalleryTitleBar(Context context) {
        this(context, null);
    }

    public KSingGalleryTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.tools_gallery_titlebar, this, true);
        initView();
    }

    private void initView() {
        mCountBg = findViewById(R.id.v_count_bg);
        mCancelView = findViewById(R.id.tv_cancel);
        mTitleView = findViewById(R.id.tv_folder_title);
        mTitleArrowView = findViewById(R.id.iv_folder_arrow);
        mCountView = findViewById(R.id.tv_choose_count);
        mContinueView = findViewById(R.id.tv_continue);
        mContinueView.setEnabled(false);
        setBackgroundColor(Color.parseColor("#00000000"));
    }

    public void hideCountBg() {
        mCountBg.setVisibility(GONE);
    }

    public void hideCountView() {
        mCountBg.setVisibility(GONE);
        mCountView.setVisibility(GONE);
    }

    public TextView getCancelView() {
        return mCancelView;
    }

    public TextView getTitleView() {
        return mTitleView;
    }

    public ImageView getTitleArrowView() {
        return mTitleArrowView;
    }

    public TextView getCountView() {
        return mCountView;
    }

    public TextView getContinueView() {
        return mContinueView;
    }

    private void setTitleArrowUp() {
        mTitleArrowView.setRotation(180);
    }

    private void setTitleArrowDown() {
        mTitleArrowView.setRotation(0);
    }

    public void rotateArrow() {
        if (up) {
            setTitleArrowDown();
        } else {
            setTitleArrowUp();
        }
        up = !up;
    }
}
