package com.godq.portal.billdetail

import com.chad.library.adapter.base.MultipleItemRvAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.godq.portal.billdetail.provider.BillDetailProvider
import com.godq.portal.billdetail.provider.BillGeneralProvider
import com.godq.portal.billdetail.provider.BillMonthDetailProvider
import com.godq.portal.billdetail.provider.BillWeekDetailProvider
import com.lazylite.mod.utils.DataBindBaseViewHolder

class BillDetailAdapter(data: List<BillEntity>?): MultipleItemRvAdapter<MultiItemEntity, DataBindBaseViewHolder>(
    data) {

    init {
        finishInitialize()
//        openLoadAnimation()
    }

    override fun getViewType(p0: MultiItemEntity?): Int {
        return p0?.itemType ?: -1
    }

    override fun registerItemProvider() {
        mProviderDelegate.registerProvider(BillGeneralProvider())
        mProviderDelegate.registerProvider(BillDetailProvider())
        mProviderDelegate.registerProvider(BillMonthDetailProvider())
        mProviderDelegate.registerProvider(BillWeekDetailProvider())
    }
}