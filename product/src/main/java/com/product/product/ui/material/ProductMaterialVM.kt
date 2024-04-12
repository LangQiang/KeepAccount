package com.product.product.ui.material

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lazylite.mod.messagemgr.MessageManager
import com.product.product.entity.ProductMaterialEntity
import com.product.product.event.IProductEvent
import com.product.product.repo.ProductRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * @author  GodQ
 * @date  2023/12/8 16:00
 */
class ProductMaterialVM: LifecycleEventObserver {

    private val repo = ProductRepo()

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var onMaterialListChangedListener: ((materialList: List<ProductMaterialEntity>) -> Unit)? = null

    private val iProductEvent: IProductEvent = object : IProductEvent {
        override fun onProductMaterialListChanged() {
            getMaterialList()
        }
    }

    fun getMaterialList(){
        scope.launch {
            onMaterialListChangedListener?.invoke(repo.getMaterialList())
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                MessageManager.getInstance().attachMessage(IProductEvent.EVENT_ID, iProductEvent)
            }
            Lifecycle.Event.ON_STOP -> {
                MessageManager.getInstance().detachMessage(IProductEvent.EVENT_ID, iProductEvent)
            }
            else -> {
                //
            }
        }
    }

}