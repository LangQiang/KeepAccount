package com.godq.portal.billdetail

import com.chad.library.adapter.base.entity.MultiItemEntity

class BillEntity(
    val type:Int,
    var expand:Boolean,
    val date: String,
    val total: Double,
    val tableTimes: Int,
    val subList:List<BillSubEntity>) : MultiItemEntity {

    companion object {
        const val TYPE_GENERAL = 100
    }

    override fun getItemType(): Int {
        return type
    }

    operator fun not(): BillEntity {
        expand = !expand
        return this
    }
}
