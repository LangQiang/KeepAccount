package com.godq.upload.chooseimage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lazylite.mod.widget.BottomRoundDialog;
import com.godq.upload.R;

/**
 * Created by lzf on 2022/1/5 4:03 下午
 */
public class ChooseImageMenuDialog extends BottomRoundDialog {
    private View mMenuView;

    public ChooseImageMenuDialog(Context context) {
        super(context);
    }

    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup rootView) {
        mMenuView = inflater.inflate(R.layout.tools_layout_choose_iamge_menu, rootView,true);
        return mMenuView;
    }

    @Override
    protected int getContainerLayoutId() {
        return R.layout.tools_layout_choose_image_menu_container;
    }

    public View getMenuView(){
        return mMenuView;
    }
}
