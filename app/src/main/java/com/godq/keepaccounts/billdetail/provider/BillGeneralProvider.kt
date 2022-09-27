package com.godq.keepaccounts.billdetail.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.godq.keepaccounts.R
import com.godq.keepaccounts.billdetail.BillEntity
import com.godq.keepaccounts.databinding.FragmentItemBillListLayoutBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class BillGeneralProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return BillEntity.TYPE_GENERAL
    }

    override fun layout(): Int {
        return R.layout.fragment_item_bill_list_layout
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is FragmentItemBillListLayoutBinding) {
            return
        }
        if (data !is BillEntity) {
            return
        }
        val binding = helper.viewDataBinding as FragmentItemBillListLayoutBinding
        binding.itemData = data
        binding.executePendingBindings()
    }

}