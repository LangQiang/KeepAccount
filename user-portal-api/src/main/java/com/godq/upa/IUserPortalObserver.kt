package com.godq.upa

import com.lazylite.mod.messagemgr.EventId
import com.lazylite.mod.messagemgr.IObserverBase

interface IUserPortalObserver: IObserverBase {
    companion object {
        val EVENT_ID = EventId { IUserPortalObserver::class.java}
    }

    fun onFetchShopList() {}
}