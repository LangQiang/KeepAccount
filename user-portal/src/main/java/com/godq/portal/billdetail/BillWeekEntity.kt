package com.godq.portal.billdetail

import com.chad.library.adapter.base.entity.MultiItemEntity

class BillWeekEntity(
    val type: Int,
    var monthDateStart: String,
    var monthDateEnd: String,
    var total:Double,
    var payOut:Double,
    var totalTablesCount: Int) : MultiItemEntity{

    var tableList = ArrayList<Int>()

    companion object {
        const val TYPE_WEEK = 103
    }

    override fun getItemType(): Int {
        return type
    }

}
