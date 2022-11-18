package com.atool.apm.dokit.component;

import android.content.Context;

import androidx.annotation.Nullable;

import com.atool.apm.R;
import com.atool.apm.dokit.ui.debugpage.DebugFragment;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.lazylite.mod.fragmentmgr.FragmentOperation;


public class DebugComponent extends AbstractKit {
    @Override
    public int getIcon() {
        return R.drawable.apm_debug_icon;
    }

    @Override
    public int getName() {
        return R.string.dokit_debug;
    }

    @Override
    public void onAppInit(@Nullable Context context) {

    }

    @Override
    public void onClick(@Nullable Context context) {

        FragmentOperation.getInstance().showFullFragment(new DebugFragment());

    }
}
