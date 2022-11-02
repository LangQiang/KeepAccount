package com.godq.portal.billdetail.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.godq.portal.R
import com.godq.portal.billdetail.BillWeekEntity
import com.godq.portal.databinding.FragmentItemBillWeekDetailLayoutBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class BillWeekDetailProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return BillWeekEntity.TYPE_WEEK
    }

    override fun layout(): Int {
        return R.layout.fragment_item_bill_week_detail_layout
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is FragmentItemBillWeekDetailLayoutBinding) {
            return
        }
        if (data !is BillWeekEntity) {
            return
        }
        val binding = helper.viewDataBinding as FragmentItemBillWeekDetailLayoutBinding
        binding.itemData = data
        binding.executePendingBindings()
    }
}