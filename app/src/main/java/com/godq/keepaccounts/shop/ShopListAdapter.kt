package com.godq.keepaccounts.shop

import com.chad.library.adapter.base.BaseQuickAdapter
import com.godq.keepaccounts.R
import com.godq.keepaccounts.databinding.FragmentItemShopListLayoutBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class ShopListAdapter(data: List<ShopEntity>?): BaseQuickAdapter<ShopEntity, DataBindBaseViewHolder>(
    R.layout.fragment_item_shop_list_layout, data) {

    override fun convert(helper: DataBindBaseViewHolder?, item: ShopEntity?) {
        (helper?.viewDataBinding as? FragmentItemShopListLayoutBinding)?.apply {
            itemData = item
            executePendingBindings()
        }
    }
}