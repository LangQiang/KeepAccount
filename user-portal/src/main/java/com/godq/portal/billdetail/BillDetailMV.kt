package com.godq.portal.billdetail

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.godq.portal.constants.getBillListUrl
import com.godq.portal.utils.HolidayRepo
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import kotlinx.coroutines.*
import timber.log.Timber

class BillDetailMV {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var onBillListDataCallback : ((List<BillEntity>?) -> Unit)? = null
    var onHolidayCallback : (() -> Unit)? = null

    fun requestShopList(shopId: String?) {
        shopId?: return
        scope.launch {
            val billDetailList = withContext(Dispatchers.IO) {
                val response = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillListUrl(shopId)))
                if (response?.isSuccessful == false) return@withContext null
                val data = response.dataToString()
                Timber.tag("shopppp").e(data)
                parseBillDetailList(data)
            }
            if (billDetailList.isNullOrEmpty()) return@launch
            onBillListDataCallback?.invoke(billDetailList)

            //获取holiday信息
            val startDate = billDetailList.first().date
            val endDate = billDetailList.last().date
            val holidayStateSuccess = withContext(Dispatchers.IO) {
                HolidayRepo.fetchHolidayStateList(startDate, endDate)
            }
            if (!holidayStateSuccess) return@launch
            onHolidayCallback?.invoke()
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