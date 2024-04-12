package com.product.product.event

import com.lazylite.mod.messagemgr.EventId
import com.lazylite.mod.messagemgr.IObserverBase


/**
 * @author  GodQ
 * @date  2023/12/26 17:25
 */
interface IProductEvent: IObserverBase {
    companion object {
        val EVENT_ID = EventId { IProductEvent::class.java }
    }

    fun onProductMaterialListChanged()
}