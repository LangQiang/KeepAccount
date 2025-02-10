package com.lazylite.media.remote;

import android.os.RemoteException;

import com.lazylite.media.remote.service.RemoteConnection;
import com.lazylite.mod.messagemgr.MessageManager;

public class AIDLLocalInterfaceImpl extends AIDLLocalInterface.Stub {
    @Override
    public void onConnect() throws RemoteException {
        MessageManager.getInstance().asyncRun(new MessageManager.Runner() {
            @Override
            public void call() {
                RemoteConnection.getInstance().onConnectCallback();
            }
        });
    }
}
