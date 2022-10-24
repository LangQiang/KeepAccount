package com.godq.msa

import com.lazylite.mod.messagemgr.EventId
import com.lazylite.mod.messagemgr.IObserverBase

interface IManagerSystemObserver: IObserverBase {
    companion object {
        val EVENT_ID = EventId { IManagerSystemObserver::class.java}
    }

    fun onBillUpdate()
}