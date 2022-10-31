package com.godq.upload.chooseimage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;


public class KwFragmentActivity extends FragmentActivity {

    private static Class<? extends Activity> topActivityClass;
    private Intent prepareIntent;
    private boolean isActive;

    public static Class<? extends Activity> getTopActivityClass() {
        if (topActivityClass == null) {
            return KwFragmentActivity.class;
        }
        return topActivityClass;
    }

    public static void setTopActivityClass(final Class<? extends Activity> activityClass) {
        topActivityClass = activityClass;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        prepareIntent = getIntent();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (getTopActivityClass() == this.getClass()) {
            setTopActivityClass(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopActivityClass(this.getClass());
        isActive = true;
        // tc9.0
        //FragmentControl.getInstance().onResume();

        if (prepareIntent != null) {
            onDispatchIntent(prepareIntent);
            prepareIntent = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
//		AppInfo.updateForgroundState();
    }

    @Override
    protected void onStop() {
        super.onStop();
//		AppInfo.updateForgroundState();
    }

    @Override
    protected final void onNewIntent(final Intent intent) {
        if (isActive) {
            onKwNewIntent(intent);
            onDispatchIntent(intent);
        } else {
            prepareIntent = intent;
        }

        super.onNewIntent(intent);
    }

    // 相当于原有的onNewIntent，应该极少用到，大多数情况用下面的onDispatchIntent
    protected void onKwNewIntent(final Intent intent) {

    }

    // 如果是用来处理onNewIntent和onCreate来的参数，用这个一起处理
    // 如果在onNewIntent里处理，则还得在onCreate里处理，这里合并到一起，方便处理
    protected void onDispatchIntent(final Intent intent) {

    }
}
