package com.godq.portal.billdetail

import com.chad.library.adapter.base.entity.MultiItemEntity

class BillMonthEntity(
    val type: Int,
    var monthDate: String,
    var total:Double,
    var payOut:Double,
    var totalTablesCount: Int) : MultiItemEntity{

    var tableList = ArrayList<Int>()

    companion object {
        const val TYPE_MONTH = 102
    }

    override fun getItemType(): Int {
        return type
    }

}
