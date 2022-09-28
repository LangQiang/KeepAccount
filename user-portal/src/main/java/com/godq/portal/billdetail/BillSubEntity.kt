package com.godq.portal.billdetail

import com.chad.library.adapter.base.entity.MultiItemEntity

data class BillSubEntity(val type:Int, val payType: String, val payAmount: String) : MultiItemEntity {
    companion object {
        const val TYPE_DETAIL = 101
    }

    override fun getItemType(): Int {
        return type
    }
}
