package com.godq.portal.billdetail

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.godq.statisticwidget.histogram.IHistogramEntity

class BillEntity(
    val type:Int,
    var expand:Boolean,
    val date: String,
    val total: Double,
    val tableTimes: Int,
    val optBy: String,
    val week: String,
    val payOut: Double,
    val bonus: Double,
    val subList:List<BillSubEntity>) : MultiItemEntity, IHistogramEntity {

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

    override fun getAbscissaText(): String {
        return date
    }

    override fun getHistogramProgress(totalLength: Float): Float {
        return (total / 15000f * totalLength).toFloat()
    }
}
