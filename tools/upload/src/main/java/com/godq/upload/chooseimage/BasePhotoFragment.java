package com.godq.upload.chooseimage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lazylite.mod.widget.BaseFragment;
import com.godq.upload.R;

/**
 * Created by lzf on 2022/1/6 11:04 上午
 */
public class BasePhotoFragment extends BaseFragment {
    private LayoutInflater mInflater;
    private FrameLayout mTitleContainer;// 用于显示标题栏
    private FrameLayout mContentContainer;// 用于显示内容
    private android.app.ProgressDialog ProgressDialog;

    protected String getTitleName() {
        return "";
    }

    /**
     * 默认不显示标题栏
     */
    protected View onCreateTitleView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    public View onCreateContentView(LayoutInflater inflater, ViewGroup container){
        return null;
    }

    protected final boolean isFragmentAlive() {
        return (getActivity() != null && !getActivity().isFinishing() && !isDetached());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mInflater = inflater;
        View view = inflater.inflate(R.layout.tools_gallery_base_fragment, container, false);
        mTitleContainer = (FrameLayout) view.findViewById(R.id.ksing_base_title_container);
        mContentContainer = (FrameLayout) view.findViewById(R.id.ksing_base_content_container);
        View titleViewGroup = onCreateTitleView(inflater, mTitleContainer);
        if (titleViewGroup != null && isFragmentAlive()) {
            mTitleContainer.addView(titleViewGroup);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View contentView = onCreateContentView(mInflater, mContentContainer);
        if (contentView != null && isFragmentAlive()) {
            mContentContainer.addView(contentView);
        }
    }

    protected final void showProcess(String text) {
        if (ProgressDialog == null) {
            try {
                ProgressDialog = new ProgressDialog(getActivity());
            } catch (Exception e) {
                ProgressDialog = null;
                e.printStackTrace();
            }
        }
        if (ProgressDialog != null) {
            ProgressDialog.setMessage(text);
            ProgressDialog.setCanceledOnTouchOutside(false);
            ProgressDialog.show();
        }
    }

    protected final void setProgressDialogCancelable(boolean cancelable) {
        if (ProgressDialog != null && ProgressDialog.isShowing()) {
            ProgressDialog.setCancelable(cancelable);
        }
    }

    protected final void hideProcess() {
        Activity activity = getActivity();
        if (ProgressDialog != null && activity != null && !activity.isFinishing()) {
            ProgressDialog.cancel();
        }
    }

    protected void setProgressTitle(String title) {
        if(ProgressDialog!=null){
            ProgressDialog.setMessage(title);
        }
    }
}
