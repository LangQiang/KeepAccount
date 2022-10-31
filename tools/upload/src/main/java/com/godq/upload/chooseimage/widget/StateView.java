package com.godq.upload.chooseimage.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.godq.upload.R;

/**
 * Created by lzf on 2022/1/4 2:30 下午
 */
public class StateView extends FrameLayout {
    private View mStateV;
    private ImageView mStateIV;
    private TextView mStateMsgTV;
    private Button mTipBtn;

    private View mLoadingV;
    private ImageView mLoadingIV;


    public StateView(@NonNull Context context) {
        super(context);
        init();
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != View.VISIBLE) {
            mLoadingV.setVisibility(View.INVISIBLE);//停止它的动画
            mTipBtn.setOnClickListener(null);
        }
    }

    public void showLoading() {
        if (mLoadingV.getVisibility() == View.VISIBLE) {
            return;
        }
        mLoadingV.setVisibility(View.VISIBLE);
        mLoadingIV.setVisibility(View.VISIBLE);
        mStateV.setVisibility(View.INVISIBLE);
        mTipBtn.setOnClickListener(null);
    }

    public void showState(int imageRes, String msg, String tipBtnStr, OnClickListener clickListener) {
        if (mLoadingV.getVisibility() == View.VISIBLE) {
            mLoadingV.setVisibility(View.INVISIBLE);
            mLoadingV.setVisibility(View.INVISIBLE);
        }
        mStateV.setVisibility(View.VISIBLE);
        if(imageRes == -1){
            mStateIV.setImageBitmap(null);
            mStateIV.setVisibility(View.GONE);
        }else {
            mStateIV.setVisibility(View.VISIBLE);
            mStateIV.setImageResource(imageRes);
        }
        mStateMsgTV.setText(msg);
        updateTipBtn(tipBtnStr, clickListener);
    }

    public void showEmpty() {
        showState(R.drawable.base_img_default, "暂无内容", "", null);
    }

    public void showError(String msg) {
        showState(R.drawable.base_img_default, msg, "", null);
    }

    private void updateTipBtn(String btnStr, OnClickListener clickListener) {
        if (TextUtils.isEmpty(btnStr) || null == clickListener) {
            mTipBtn.setVisibility(View.GONE);
            mTipBtn.setOnClickListener(null);
        }
        mTipBtn.setVisibility(View.VISIBLE);
        mTipBtn.setOnClickListener(clickListener);
        mTipBtn.setText(btnStr);
    }

    private void init() {
        inflate();
        mTipBtn = findViewById(R.id.top_button);
        mStateV = findViewById(R.id.ll_state);
        mStateIV = findViewById(R.id.iv_state);
        mStateMsgTV = findViewById(R.id.tv_state_msg);
        mLoadingV = findViewById(R.id.fl_loading);
        mLoadingIV = findViewById(R.id.v_loading);
    }

    private void inflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.tools_layout_state_view, this);
    }
}
