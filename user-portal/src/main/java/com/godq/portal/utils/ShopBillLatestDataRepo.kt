package com.godq.portal.utils

import com.godq.portal.billdetail.BillEntity
import com.godq.portal.billdetail.parseBillDetailList
import com.godq.portal.constants.getShopLatestDataListUrl
import com.google.gson.reflect.TypeToken
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.utils.GsonUtil
import java.util.concurrent.ConcurrentHashMap

object ShopBillLatestDataRepo {

    private val localBillLatestDataList = ConcurrentHashMap<String, String>()

    private val shopBillLatestDataList = ConcurrentHashMap<String, BillEntity>()

    private var isLocalInit = false

    fun initLocalData() {
        val localData = ConfMgr.getStringValue("", "local_shop_latest_data", "{}")
        localBillLatestDataList.clear()
        localBillLatestDataList.putAll(GsonUtil.getGson().fromJson(localData, object : TypeToken<ConcurrentHashMap<String, String>>(){}.type))
        isLocalInit = true
    }

    fun fetchShopBillLatestDataList(shopIds: Array<String>?): Boolean {
        shopIds?: return false
        val response = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getShopLatestDataListUrl(shopIds)))
        if (response?.isSuccessful == false) return false
        val data = response.dataToString()
        updateList(data)
        return true
    }

    private fun updateList(data: String) {
        shopBillLatestDataList.clear()
        parseBillDetailList(data)?.forEach {
            shopBillLatestDataList[it.shopId] = it
        }
    }

    fun isLatestData(shopId: String): Boolean {
        if (!isLocalInit) return true
        val remoteDate = shopBillLatestDataList[shopId]?.date?: return true
        return localBillLatestDataList[shopId] == remoteDate
    }

    fun syncDataLatest(shopId: String) {
        val newDate = shopBillLatestDataList[shopId]?.date?: return
        localBillLatestDataList[shopId] = newDate
        val saveData = GsonUtil.getGson().toJson(localBillLatestDataList)

        ConfMgr.setStringValue("", "local_shop_latest_data", saveData, false)
    }
}