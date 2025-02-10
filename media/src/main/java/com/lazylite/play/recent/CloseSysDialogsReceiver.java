package com.lazylite.play.recent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.godq.media_api.media.PlayStatus;
import com.lazylite.media.playctrl.PlayControllerImpl;
import com.lazylite.mod.log.LogMgr;

public class CloseSysDialogsReceiver extends BroadcastReceiver {
    private long interval = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            String reason = intent.getStringExtra("reason");

            if (reason == null)
                return;

            // Home键
            if (reason.equals("homekey")) {
                return;
            }

            // 最近任务列表键
            if (reason.equals("recentapps")/* && AppInfo.IS_FORGROUND*/) {
                try {
                    long cur = System.currentTimeMillis();
                    if (cur - interval > 5000) {
                        interval = cur;
                        if (PlayControllerImpl.getInstance().getPlayStatus() == PlayStatus.STATUS_PLAYING) {
                            RecentImpl.getInstance().saveRecent(RecentBean.CreateRecent(
                                    PlayControllerImpl.getInstance().getCurrentBook(),
                                    PlayControllerImpl.getInstance().getCurrentChapter(),
                                    PlayControllerImpl.getInstance().getCurrentProgress(),
                                    System.currentTimeMillis()
                            ));
                            LogMgr.e("CloseSysDialogsReceiver", "saveRecent");
                        } else {
                            LogMgr.e("CloseSysDialogsReceiver", "not playing");
                        }
                    } else {
                        LogMgr.e("CloseSysDialogsReceiver", "under time");
                    }
                } catch (Exception e) {
                    LogMgr.e("CloseSysDialogsReceiver", "exception:" + e);
                }
            }
        }
    }
}
