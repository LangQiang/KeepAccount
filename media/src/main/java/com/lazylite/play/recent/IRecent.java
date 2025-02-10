package com.lazylite.play.recent;

public interface IRecent {

    void saveRecent(RecentBean recentBean);

    void getLastRecent(RecentImpl.OnGetLastRecentListener onGetLastRecentListener);

    void clearRecent();
}
