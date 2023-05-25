package com.tme.upgrade.ui.force;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.tme.upgrade.R;
import com.tme.upgrade.databinding.UpgradeForceDialogLayoutBinding;
import com.tme.upgrade.entity.UpgradeInfo;
import com.tme.upgrade.mgr.UpgradeMgr;

public class ForceUpgradeDialog extends Dialog {

    private final Activity activity;

    private final UpgradeInfo upgradeInfo;

    public ForceUpgradeDialog(@NonNull Activity context, UpgradeInfo upgradeInfo) {
        super(context, R.style.UpgradeAlertTheme);
        this.activity = context;
        this.upgradeInfo = upgradeInfo;
        setContentView(initView());
        setCancelable(false);
    }

    private View initView() {
        UpgradeForceDialogLayoutBinding binding = UpgradeForceDialogLayoutBinding.inflate(LayoutInflater.from(getContext()), null, false);
        binding.setInfo(upgradeInfo);
        binding.ok.setOnClickListener(v -> UpgradeMgr.getInstance().installApk());
        return binding.getRoot();
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
