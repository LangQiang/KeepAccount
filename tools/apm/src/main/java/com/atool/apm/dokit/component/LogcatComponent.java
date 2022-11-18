package com.atool.apm.dokit.component;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.atool.apm.R;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.lazylite.mod.App;
import com.lazylite.mod.utils.TestShowLog;


public class LogcatComponent extends AbstractKit {
    @Override
    public int getIcon() {
        return R.drawable.apm_logcat_icon;
    }

    @Override
    public int getName() {
        return R.string.dokit_log;
    }

    @Override
    public void onAppInit(Context context) {

    }

    @Override
    public void onClick(Context context) {
        Activity activity = App.getMainActivity();
        if (activity instanceof AppCompatActivity) {
            TestShowLog.showOrHideLogView((AppCompatActivity)activity);
        }
    }
}
