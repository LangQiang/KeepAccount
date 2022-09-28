package com.godq.keepaccounts.billdetail

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.godq.keepaccounts.constants.getBillListUrl
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import timber.log.Timber

class BillDetailMV {

    var onBillListDataCallback : ((List<BillEntity>?) -> Unit)? = null

    fun requestShopList(shopId: String?) {
        shopId?: return
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet(getBillListUrl(shopId))) {
            val data = it.dataToString()
            Timber.tag("shopppp").e(data)
            onBillListDataCallback?.invoke(parseBillDetailList(data))
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
        itemData: BillEntity) {
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