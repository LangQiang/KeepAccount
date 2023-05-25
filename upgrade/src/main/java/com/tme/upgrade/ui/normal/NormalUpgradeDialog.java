package com.tme.upgrade.ui.normal;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.tme.upgrade.R;
import com.tme.upgrade.databinding.UpgradeDialogLayoutBinding;

public class NormalUpgradeDialog extends Dialog {

    private final Activity activity;

    public NormalUpgradeDialog(@NonNull Activity context) {
        super(context, R.style.UpgradeAlertTheme);
        this.activity = context;
        UpgradeDialogLayoutBinding viewDataBinding = UpgradeDialogLayoutBinding.inflate(LayoutInflater.from(context), null, false);
        setContentView(viewDataBinding.getRoot());
        UpgradeViewModel upgradeViewModel = new UpgradeViewModel(viewDataBinding);
        viewDataBinding.setData(upgradeViewModel);
        viewDataBinding.setView(this);
        viewDataBinding.executePendingBindings();

        setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        if (activity != null && !activity.isFinishing()) {
            super.show();
            setWindowParam();
        }
    }

    @Override
    public void dismiss() {
        if (activity != null && !activity.isFinishing()) {
            super.dismiss();
        }
    }

    private void setWindowParam() {
        Window w = getWindow();
        if (w == null) {
            return;
        }
        w.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = (int) (w.getContext().getResources().getDisplayMetrics().widthPixels * 0.86f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setAttributes(lp);
    }
}
