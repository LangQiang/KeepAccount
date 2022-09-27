package com.godq.keepaccounts.billdetail.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.godq.keepaccounts.R
import com.godq.keepaccounts.billdetail.BillSubEntity
import com.godq.keepaccounts.databinding.FragmentItemBillDetailLayoutBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class BillDetailProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return BillSubEntity.TYPE_DETAIL
    }

    override fun layout(): Int {
        return R.layout.fragment_item_bill_detail_layout
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is FragmentItemBillDetailLayoutBinding) {
            return
        }
        if (data !is BillSubEntity) {
            return
        }
        val binding = helper.viewDataBinding as FragmentItemBillDetailLayoutBinding
        binding.itemData = data
        binding.executePendingBindings()
    }
}