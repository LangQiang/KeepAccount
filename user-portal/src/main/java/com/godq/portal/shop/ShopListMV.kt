package com.godq.portal.shop

import com.godq.portal.constants.getShopListUrl
import com.godq.portal.utils.jumpToBillDetail
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import timber.log.Timber

class ShopListMV {

    var onShopListDataCallback : ((List<ShopEntity>?) -> Unit)? = null

    fun requestShopList() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet(getShopListUrl())) {
            val data = it.dataToString()
            Timber.tag("shopppp").e("url:${it.finalRequestUrl}\n$data")
            onShopListDataCallback?.invoke(parseShopList(data))
        }
    }

    fun onItemClick(shopEntity: ShopEntity?) {
        shopEntity ?: return
        jumpToBillDetail(shopId = shopEntity.id)
    }
}