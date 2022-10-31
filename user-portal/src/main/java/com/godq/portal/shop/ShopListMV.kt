package com.godq.portal.shop

import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.godq.accountsa.IAccountService
import com.godq.portal.UserPortalLinkHelper
import com.godq.portal.common.DataLoadingStateVm
import com.godq.portal.constants.getShopListUrl
import com.godq.portal.utils.jumpToBillDetail
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.receiver.network.NetworkStateUtil
import timber.log.Timber

class ShopListMV : LifecycleEventObserver, DataLoadingStateVm {

    override val loadUIStateType: ObservableField<Int> = ObservableField(DataLoadingStateVm.LOAD_TYPE_LOADING)

    var onShopListDataCallback : ((List<ShopEntity>?) -> Unit)? = null

    private val accountObserver = object : IAccountService.IAccountObserver {
        override fun onLogin() {
            Timber.tag("account").e("onLogin")
            requestShopList()
        }

        override fun onLogout() {
            Timber.tag("account").e("onLogout")
            setDataLoadingState(DataLoadingStateVm.LOAD_TYPE_NOT_LOGIN)
        }

        override fun onUpdate() {

        }

    }

    fun requestShopList() {
        if (!UserPortalLinkHelper.isLogin()) {
            setDataLoadingState(DataLoadingStateVm.LOAD_TYPE_NOT_LOGIN)
            return
        }
        if (!NetworkStateUtil.isAvailable()) {
            setDataLoadingState(DataLoadingStateVm.LOAD_TYPE_NET_ERROR)
            return
        }
        setDataLoadingState(DataLoadingStateVm.LOAD_TYPE_LOADING)
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet(getShopListUrl())) {
            val data = it.dataToString()
            Timber.tag("shopppp").e("url:${it.finalRequestUrl}\n$data")
            parseShopList(data).apply {
                if (isNullOrEmpty()) {
                    setDataLoadingState(DataLoadingStateVm.LOAD_TYPE_EMPTY)
                } else {
                    setDataLoadingState(DataLoadingStateVm.LOAD_TYPE_HAS_CONTENT)
                }
                onShopListDataCallback?.invoke(this)
            }
        }
    }

    fun onItemClick(shopEntity: ShopEntity?) {
        shopEntity ?: return
        jumpToBillDetail(shopId = shopEntity.id)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Timber.tag("test").e(event.name)
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                MessageManager.getInstance().attachMessage(IAccountService.IAccountObserver.EVENT_ID, accountObserver)
            }
            Lifecycle.Event.ON_DESTROY -> {
                MessageManager.getInstance().detachMessage(IAccountService.IAccountObserver.EVENT_ID, accountObserver)
            }
            Lifecycle.Event.ON_RESUME -> {

            }
            else -> {
                //ignore
            }
        }
    }

}