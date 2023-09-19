package com.godq.portal.billdetail

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun parseBillDetailList(jsonData: String?): List<BillEntity>? {
    jsonData ?: return null
    return try {
        val retList = ArrayList<BillEntity>()
        val obj = JSONObject(jsonData)
        if (obj.optInt("code") != 200) {
            return null
        }
        val array = obj.optJSONArray("data") ?: return null

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val weekDays = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")

        for (i in 0 until array.length()) {
            val itemObj = array.optJSONObject(i) ?: continue
            val subList = ArrayList<BillSubEntity>()
            itemObj.optJSONArray("bill_pay_type_list")?.apply {
                for (j in 0 until length()) {
                    val typeJson = optJSONObject(j)
                    val type = typeJson?.optString("bill_type") ?: "未知"
                    val amount = typeJson?.optString("bill_amount") ?: "0.0"
                    subList.add(BillSubEntity(BillSubEntity.TYPE_DETAIL, type, amount))
                }
            }
            val date = itemObj.optString("bill_date")
            var week = "未知"

            simpleDateFormat.parse(date)?.apply {
                calendar.time = this
                week = weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            }

            retList.add(
                BillEntity(
                    BillEntity.TYPE_GENERAL,
                    false,
                    date,
                    itemObj.optDouble("bill_total"),
                    itemObj.optInt("bill_table_times"),
                    itemObj.optString("bill_opt_by").let { if (TextUtils.isEmpty(it)) "未知" else it },
                    week,
                    itemObj.optDouble("bill_pay_out", 0.0),
                    subList
                )
            )
        }

        return retList
    } catch (e: Exception) {
        null
    }
}

fun transformByListType(
    billDetailList: List<BillEntity>,
    listType: String
): List<MultiItemEntity> {
    return when (listType) {
        BillDetailFragment.LIST_TYPE_DAY -> {
            billDetailList
        }
        BillDetailFragment.LIST_TYPE_MONTH -> {
            transformToMonthList(billDetailList)
        }
        BillDetailFragment.LIST_TYPE_WEEK -> {
            transformToWeekList(billDetailList)
        }
        else -> {
            billDetailList
        }
    }
}

private fun transformToMonthList(billDetailList: List<BillEntity>): List<MultiItemEntity> {
    val monthList = ArrayList<BillMonthEntity>()
    var month = ""
    var billMonthEntity: BillMonthEntity? = null
    for (billEntity in billDetailList) {
        val itemMonth = billEntity.date.substring(0, 7)
        if (itemMonth != month) {
            month = itemMonth
            billMonthEntity = BillMonthEntity(BillMonthEntity.TYPE_MONTH, month, 0.0, 0.0, 0)
            billMonthEntity.totalTablesCount += billEntity.tableTimes
            billMonthEntity.total += billEntity.total
            billMonthEntity.payOut += billEntity.payOut
            billMonthEntity.tableList.add(billEntity.tableTimes)
            monthList.add(billMonthEntity)
        } else {
            billMonthEntity?: continue
            billMonthEntity.totalTablesCount += billEntity.tableTimes
            billMonthEntity.tableList.add(billEntity.tableTimes)
            billMonthEntity.total += billEntity.total
            billMonthEntity.payOut += billEntity.payOut
        }
    }
    return monthList
}

private fun transformToWeekList(billDetailList: List<BillEntity>): List<MultiItemEntity> {
    val weekList = ArrayList<BillWeekEntity>()
    var isFirst = true
    var billWeekEntity: BillWeekEntity? = null
    for (billEntity in billDetailList.reversed()) {
        val week = billEntity.week
        if (week == "星期一" || isFirst) {
            isFirst = false
            billWeekEntity = BillWeekEntity(BillWeekEntity.TYPE_WEEK, billEntity.date, "", 0.0, 0.0, 1, 0)
            billWeekEntity.totalTablesCount += billEntity.tableTimes
            billWeekEntity.total += billEntity.total
            billWeekEntity.payOut += billEntity.payOut
            billWeekEntity.tableList.add(billEntity.tableTimes)
            weekList.add(billWeekEntity)
        } else {
            billWeekEntity?: continue
            if (week == "星期日") {
                billWeekEntity.monthDateEnd = billEntity.date
            }
            billWeekEntity.daysOfThisWeek++
            billWeekEntity.totalTablesCount += billEntity.tableTimes
            billWeekEntity.tableList.add(billEntity.tableTimes)
            billWeekEntity.total += billEntity.total
            billWeekEntity.payOut += billEntity.payOut
        }
    }
    return weekList.reversed()
}