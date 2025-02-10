package com.lazylite.play.recent;


import android.os.Handler;
import android.os.Message;

import androidx.annotation.MainThread;

import com.godq.threadpool.ThreadPool;
import com.google.gson.Gson;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.utils.IOUtils;
import com.lazylite.mod.utils.KwDirs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.ref.WeakReference;
import java.util.TreeSet;

//先用文件来实现
class RecentIO {

    private static final int SAVE_LIMIT = 10;

    private final RecentIOHandler recentIOHandler = new RecentIOHandler(this);


    private final TreeSet<RecentBean> sSafeSet = new TreeSet<>((o1, o2) -> {
        long diff = o2.getLastAccessTime() - o1.getLastAccessTime();
        if (diff == 0) {
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    });

    @MainThread
    public void save(RecentBean recentBean) {

        startSave(recentBean);

    }

    private void startSave(RecentBean recentBean) {
        if (sSafeSet.isEmpty()) {
            sSafeSet.add(recentBean);
            ThreadPool.exec(() -> {
                realSave(recentBean);
                //finish
                Message obtain = Message.obtain();
                obtain.obj = recentBean;
                obtain.what = RecentIOHandler.WHAT_SAVE_FINISH;
                recentIOHandler.sendMessage(obtain);
            });
        } else {
            sSafeSet.add(recentBean);
        }
    }

    private void internalLoop() {
        if (sSafeSet.isEmpty()) {
            return;
        }

        final RecentBean waitToSave = sSafeSet.first();

        ThreadPool.exec(() -> {

            realSave(waitToSave);
            //finish
            Message obtain = Message.obtain();
            obtain.obj = waitToSave;
            obtain.what = RecentIOHandler.WHAT_SAVE_FINISH;
            recentIOHandler.sendMessage(obtain);
        });
    }

    private synchronized void realSave(RecentBean waitToSave) {

        String path = getSavePath();

        TreeSet<RecentBean> allList = readRecentList(path, SAVE_LIMIT);

        RecentBean sameBean = null;
        for (RecentBean recentBean : allList) {
            if (recentBean.getChapterBean().mRid == waitToSave.getChapterBean().mRid) {
                sameBean = recentBean;
                break;
            }
        }
        if (sameBean == null) {
            allList.add(waitToSave);
        } else {
            allList.remove(sameBean);
            sameBean.setProgress(waitToSave.getProgress());
            sameBean.setBookBean(waitToSave.getBookBean());
            sameBean.setChapterBean(waitToSave.getChapterBean());
            sameBean.setLastAccessTime(waitToSave.getLastAccessTime());
            allList.add(sameBean);
        }

        saveAllRecentList(allList, path);
    }

    private void saveAllRecentList(TreeSet<RecentBean> allList, String path) {

        BufferedWriter writer = null;

        try {

            writer = new BufferedWriter(new FileWriter(path));

            for (RecentBean recentBean : allList) {
                writer.write(new Gson().toJson(recentBean));
                writer.newLine();
            }

        } catch (Exception e) {
            createSavePath();
        } finally {
            IOUtils.closeIoStream(writer);
        }
    }


    private TreeSet<RecentBean> readRecentList(String path, int limit) {

        TreeSet<RecentBean> recentBeans = new TreeSet<>((o1, o2) -> {
            long diff = o2.getLastAccessTime() - o1.getLastAccessTime();
            if (diff == 0) {
                return 0;
            } else {
                return diff > 0 ? 1 : -1;
            }
        });

        if (!new File(path).exists()) {
            return recentBeans;
        }

        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new FileReader(path));

            String line;

            int count = 0;

            while ((line = reader.readLine()) != null) {
                recentBeans.add(new Gson().fromJson(line, RecentBean.class));

                if (count++ >= limit) {
                    break;
                }
            }

        } catch (Exception e) {
            createSavePath();
        } finally {
            IOUtils.closeIoStream(reader);
        }
        return recentBeans;
    }

    private void removeSavedData(RecentBean recentBean) {
        sSafeSet.remove(recentBean);
    }

    @MainThread
    public void fetchRecentList(IOListener ioListener) {
        if (ioListener == null) {
            return;
        }
        ThreadPool.exec(() -> {
            TreeSet<RecentBean> recentBeans;
            synchronized (RecentIO.this) {
                recentBeans = readRecentList(getSavePath(), 1);
            }
            MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
                @Override
                public void call() {
                    ioListener.onFetch(recentBeans);
                }
            });
        });
    }

    public void clearRecentFile() {
        Message obtain = Message.obtain();
        obtain.what = RecentIOHandler.WHAT_CLEAR;
        recentIOHandler.sendMessage(obtain);
    }

    public interface IOListener {

        void onFetch(TreeSet<RecentBean> recentBeans);

    }

    private static class RecentIOHandler extends Handler {

        public static final int WHAT_SAVE_FINISH = 0;
        public static final int WHAT_CLEAR = 1;

        private final WeakReference<RecentIO> weakReference;

        private RecentIOHandler(RecentIO recentIO) {
            weakReference = new WeakReference<>(recentIO);
        }

        @Override
        public void handleMessage(Message msg) {
            RecentIO recentIO = weakReference.get();
            if (recentIO == null) {
                return;
            }

            if (msg == null) {
                return;
            }

            if (msg.what == WHAT_SAVE_FINISH) {
                recentIO.removeSavedData((RecentBean) msg.obj);
                recentIO.internalLoop();
            } else if (msg.what == WHAT_CLEAR) {
                recentIO.deleteRecentFile();
            }
        }
    }

    private void deleteRecentFile() {
        File waitDelete = new File(getSavePath());
        if (waitDelete.exists()) {
            boolean delete = waitDelete.delete();
            if (delete) {
                LogMgr.e("Recent", "delete file suc");
            }
        }
    }

    private String getSavePath() {
        return KwDirs.getDir(KwDirs.UPDATE) + "recent.info";
    }

    private void createSavePath() {
        try {
            File file = new File(KwDirs.getDir(KwDirs.UPDATE));
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (mkdirs) {
                    LogMgr.e("Recent", "mkdirs suc");
                }
            }
        } catch (Exception ignore) {

        }
    }
}
