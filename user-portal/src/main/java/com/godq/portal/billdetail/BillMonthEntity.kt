package com.godq.portal.billdetail

import com.chad.library.adapter.base.entity.MultiItemEntity

class BillMonthEntity(
    val type: Int,
    var monthDate: String,
    var total:Double,
    var payOut:Double,
    var totalTablesCount: Int) : MultiItemEntity{

    var tableList = ArrayList<Int>()

    //月内真实营运天数
    var daysCountOfMonth: Int = 0
    //月内人工费用
    var payOutForLabor: Double = 0.0
    //月内水电气费用
    var payOutForWEG: Double = 0.0
    //月内房租费用
    var payOutForRent: Double = 0.0

    companion object {
        const val TYPE_MONTH = 102
    }

    override fun getItemType(): Int {
        return type
    }

}
