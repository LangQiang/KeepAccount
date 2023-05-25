package com.godq.portal.billdetail

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.godq.portal.constants.getBillListUrl
import com.godq.portal.utils.HolidayRepo
import com.godq.portal.utils.WeatherRepo
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import kotlinx.coroutines.*
import timber.log.Timber

class BillDetailMV {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var onBillListDataCallback : ((List<MultiItemEntity>?) -> Unit)? = null
    var onHolidayCallback : (() -> Unit)? = null

    private var cacheList: List<BillEntity>? = null

    fun requestShopList(shopId: String?, listType: String?) {
        listType?: return
        shopId?: return
        scope.launch {
            val billDetailList = withContext(Dispatchers.IO) {
                val response = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillListUrl(shopId)))
                if (response?.isSuccessful == false) return@withContext null
                val data = response.dataToString()
                Timber.tag("shopppp").e(data)
                parseBillDetailList(data)?.let {
                    if (it.size <= 1) it else it.reversed()
                }
            }
            if (billDetailList.isNullOrEmpty()) return@launch
            cacheList = billDetailList
            val finalList = transformByListType(billDetailList, listType)
            onBillListDataCallback?.invoke(finalList)

            //获取holiday信息
            val first = (billDetailList.first() as? BillEntity) ?: return@launch
            val last = (billDetailList.last() as? BillEntity) ?: return@launch
            val startDate = last.date
            val endDate = first.date
            val holidayStateSuccess = withContext(Dispatchers.IO) {
                HolidayRepo.fetchHolidayStateList(startDate, endDate)
            }
            val weatherStateSuc = withContext(Dispatchers.IO) {
                WeatherRepo.fetchHolidayStateList(startDate, endDate)
            }
            if (!holidayStateSuccess || !weatherStateSuc) return@launch
            onHolidayCallback?.invoke()
        }
    }

    fun changeListType(shopId: String?, listType: String) {
        val list = cacheList
        if (list == null) {
            requestShopList(shopId, listType)
        } else {
            onBillListDataCallback?.invoke(transformByListType(list, listType))
        }
    }


    fun onItemClick(adapter: BaseQuickAdapter<Any, BaseViewHolder>?, position: Int) {
        val dataList = adapter?.data ?: return
        if (position < 0 || position >= dataList.size) return
        val itemData: BillEntity = !((dataList[position] as? BillEntity) ?: return)

        if (itemData.expand) expand(adapter, dataList, position, itemData) else collapse(adapter, dataList, position, itemData)
    }

    private fun collapse(
        adapter: BaseQuickAdapter<Any, BaseViewHolder>,
        dataList: MutableList<Any>,
        position: Int,
        itemData: BillEntity
    ) {
        for (i in itemData.subList.indices) {
            dataList.removeAt(position + 1)
        }
        adapter.notifyItemChanged(position)
        adapter.notifyItemRangeRemoved(position + 1, itemData.subList.size)
    }

    private fun expand(
        adapter: BaseQuickAdapter<Any, BaseViewHolder>,
        dataList: MutableList<Any>,
        position: Int,
        itemData: BillEntity
    ) {
        dataList.addAll(position + 1, itemData.subList)
        adapter.notifyItemChanged(position, "changeExpand")
        adapter.notifyItemRangeInserted(position + 1, itemData.subList.size)

    }
}