package com.godq.cms.asset

import com.lazylite.mod.messagemgr.EventId
import com.lazylite.mod.messagemgr.IObserverBase


/**
 * @author  GodQ
 * @date  2023/5/19 5:18 下午
 */
interface IAssetEvent: IObserverBase {

    companion object {
        val EVENT_ID = EventId { IAssetEvent::class.java }
    }

    fun onAddAsset()
}