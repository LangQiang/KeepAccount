package com.lazylite.play.playpage.widget.seekbar;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * @author qyh
 * email：yanhui.qiao@kuwo.cn
 * @date 2021/6/4.
 * description：生命周期抽象类
 */
public abstract class LifeclycleHelper implements LifecycleObserver {

    private Lifecycle mLifecycle;

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestory() {

    }

    public void setLifecycleOwner(@Nullable LifecycleOwner owner) {
        if (owner == null) {
            clearLifecycleObserver();
        } else {
            clearLifecycleObserver();
            mLifecycle = owner.getLifecycle();
            mLifecycle.addObserver(this);
        }
    }

    private void clearLifecycleObserver() {
        if (mLifecycle != null) {
            mLifecycle.removeObserver(this);
            mLifecycle = null;
        }
    }
}
