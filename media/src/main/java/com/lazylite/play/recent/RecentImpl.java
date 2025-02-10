package com.lazylite.play.recent;

import android.content.Intent;
import android.content.IntentFilter;

import com.lazylite.mod.App;

import java.util.List;
import java.util.TreeSet;

public class RecentImpl implements IRecent {

    private final RecentIO recentIO;

    private CloseSysDialogsReceiver closeSysDialogsReceiver;

    private RecentImpl() {
        recentIO = new RecentIO();
        registerCloseSysDialogReceiver();
    }

    public static IRecent getInstance() {
        return Inner.recent;
    }

    @Override
    public void saveRecent(RecentBean recentBean) {
        recentIO.save(recentBean);
    }

    @Override
    public void getLastRecent(OnGetLastRecentListener onGetLastRecentListener) {

        if (onGetLastRecentListener == null) {
            return;
        }

        recentIO.fetchRecentList(onGetLastRecentListener::onFetch);
    }

    @Override
    public void clearRecent() {
        recentIO.clearRecentFile();
    }

    private void registerCloseSysDialogReceiver() {
        if (closeSysDialogsReceiver == null) {
            closeSysDialogsReceiver = new CloseSysDialogsReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            App.getInstance().registerReceiver(closeSysDialogsReceiver, filter);
        }
    }

    private static class Inner {
        private static final IRecent recent = new RecentImpl();
    }

    public interface OnGetLastRecentListener {
        void onFetch(TreeSet<RecentBean> recentBeans);
    }


}
