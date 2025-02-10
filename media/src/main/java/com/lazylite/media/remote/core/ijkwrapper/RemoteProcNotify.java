package com.lazylite.media.remote.core.ijkwrapper;

import com.godq.media_api.media.IPlayObserver;
import com.lazylite.mod.messagemgr.MessageManager;

public class RemoteProcNotify {
    public static void notifyPrePlay(boolean isLocal) {
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.onPreStart(isLocal);
            }
        });
    }

    public static void notifyPause() {
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.onPause();
            }
        });
    }

    public static void notifyStop(boolean end) {
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.onStop(end);
            }
        });
    }

    public static void notifyRealPlay() {
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.onRealPlay();
            }
        });
    }

    public static void notifyContinue() {
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.onContinue();
            }
        });
    }

    public static void notifyFailed(String name) {
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.onFailed(-1, name);
            }
        });
    }

    public static void notifyPlay() {
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.onPlay();
            }
        });
    }

    public static void notifyClearPlayList() {
        MessageManager.getInstance().asyncNotify(IPlayObserver.EVENT_ID, new MessageManager.Caller<IPlayObserver>() {
            @Override
            public void call() {
                ob.clearPlayList();
            }
        });
    }
}
