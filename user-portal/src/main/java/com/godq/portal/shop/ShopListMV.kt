package com.godq.keepaccounts.shop

import com.godq.keepaccounts.constants.getShopListUrl
import com.godq.keepaccounts.utils.jumpToBillDetail
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import timber.log.Timber

class ShopListMV {

    var onShopListDataCallback : ((List<ShopEntity>?) -> Unit)? = null

    fun requestShopList() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet(getShopListUrl())) {
            val data = it.dataToString()
            Timber.tag("shopppp").e(data)
            onShopListDataCallback?.invoke(parseShopList(data))
        }
    }

    fun onItemClick(shopEntity: ShopEntity?) {
        shopEntity ?: return
        jumpToBillDetail(shopId = shopEntity.id)
    }
}