//package com.atool.apm.bugly;
//
//import android.content.Context;
//
//import androidx.fragment.app.Fragment;
//
//import com.lazylite.mod.App;
//import com.lazylite.mod.fragmentmgr.FragmentOperation;
//import com.lazylite.mod.utils.AppInfo;
//import com.tencent.bugly.crashreport.CrashReport;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class BuglyHelper {
//
//    public static void initBugly(Context context) {
//        CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(context.getApplicationContext());
//        userStrategy.setUploadProcess(App.isMainProcess());
//        userStrategy.setAppChannel(AppInfo.INSTALL_CHANNEL);
//        userStrategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
//            @Override
//            public synchronized Map<String, String> onCrashHandleStart(int i, String s, String s1, String s2) {
//                Map<String, String> map = new HashMap<>();
//                map.put("currentPage", getTopFragment());
//                return map;
//            }
//
//        });
//        CrashReport.initCrashReport(context.getApplicationContext(), "5cfd74d117", false, userStrategy);
//    }
//
//    private static String getTopFragment() {
//        Fragment topFragment = FragmentOperation.getInstance().getTopFragment();
//        return topFragment == null ? "unknown" : topFragment.getClass().getName();
//    }
//}
