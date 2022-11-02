package com.godq.portal.billdetail.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.godq.portal.R
import com.godq.portal.billdetail.BillMonthEntity
import com.godq.portal.databinding.FragmentItemBillMonthDetailLayoutBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class BillMonthDetailProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return BillMonthEntity.TYPE_MONTH
    }

    override fun layout(): Int {
        return R.layout.fragment_item_bill_month_detail_layout
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is FragmentItemBillMonthDetailLayoutBinding) {
            return
        }
        if (data !is BillMonthEntity) {
            return
        }
        val binding = helper.viewDataBinding as FragmentItemBillMonthDetailLayoutBinding
        binding.itemData = data
        binding.executePendingBindings()
    }
}