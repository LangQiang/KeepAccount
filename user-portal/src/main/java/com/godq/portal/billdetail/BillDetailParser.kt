package com.godq.portal.billdetail

import android.text.TextUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.godq.portal.ext.safeToDouble
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

val subEntitySortFormatArr = mapOf(
    "银行卡" to 0,
    "支付宝" to 10,
    "微信" to 20,
    "现金" to 30,
    "美团" to 40,
    "美团套餐" to 44,
    "美团代金券" to 46,
    "抖音" to 50,
    "外卖" to 60,
    "免单" to 70,
    "其他" to 80,
    "支出" to 90,
    "美团扣点" to 95,
    "抖音扣点" to 96,
    "食材" to 100,
    "人工" to 110,
    "房租" to 115,
    "水费" to 120,
    "电费" to 130,
    "燃气" to 140,
    "其他支出" to 150,
    "分红" to 160,
)
val subEntityAmountForceNegativeSet = setOf(
    "支出",
    "美团扣点",
    "抖音扣点",
    "食材",
    "人工",
    "房租",
    "水费",
    "电费",
    "燃气",
    "其他支出",
)

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
            val subList = TreeSet<BillSubEntity>(kotlin.Comparator { o1, o2 ->
//                return@Comparator if (o1.sortWeight > o2.sortWeight) {
//                    1
//                }else if (o1.sortWeight < o2.sortWeight) {
//                    -1
//                } else 0
                return@Comparator o1.sortWeight - o2.sortWeight
            })

            var unknownTypeWeight = -1
            itemObj.optJSONArray("bill_pay_type_list")?.apply {
                for (j in 0 until length()) {
                    val typeJson = optJSONObject(j)
                    val type = typeJson?.optString("bill_type") ?: "未知"
                    val amount = typeJson?.optString("bill_amount") ?: "0.0"
                    subList.add(
                            BillSubEntity(BillSubEntity.TYPE_DETAIL,
                            type,
                            if (subEntityAmountForceNegativeSet.contains(type))  "-$amount" else amount,
                            subEntitySortFormatArr[type] ?: unknownTypeWeight--)
                    )
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
                    itemObj.optString("bill_shop_id"),
                    date,
                    itemObj.optDouble("bill_total"),
                    itemObj.optInt("bill_table_times"),
                    itemObj.optString("bill_opt_by").let { if (TextUtils.isEmpty(it)) "未知" else it },
                    week,
                    itemObj.optDouble("bill_pay_out", 0.0),
                    itemObj.optDouble("bill_bonus", 0.0),
                    subList.toList()
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
            resetBillDetailList(billDetailList)
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

private fun resetBillDetailList(billDetailList: List<BillEntity>): List<MultiItemEntity> {
    for (billEntity in billDetailList) {
        billEntity.expand = false
    }
    return billDetailList
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
            billMonthEntity.daysCountOfMonth++
            billMonthEntity.totalTablesCount += billEntity.tableTimes
            billMonthEntity.total += billEntity.total
            billMonthEntity.payOut += billEntity.payOut
            for (billSubEntity in billEntity.subList) {
                if (billSubEntity.payType == "人工") {
                    billMonthEntity.payOutForLabor += billSubEntity.payAmount.safeToDouble().absoluteValue
                }
                if (billSubEntity.payType == "水费" || billSubEntity.payType == "电费") {
                    billMonthEntity.payOutForWEG += billSubEntity.payAmount.safeToDouble().absoluteValue
                }
                if (billSubEntity.payType == "房租") {
                    billMonthEntity.payOutForRent += billSubEntity.payAmount.safeToDouble().absoluteValue
                }

            }
            billMonthEntity.tableList.add(billEntity.tableTimes)
            monthList.add(billMonthEntity)
        } else {
            billMonthEntity?: continue
            billMonthEntity.daysCountOfMonth++
            billMonthEntity.totalTablesCount += billEntity.tableTimes
            billMonthEntity.tableList.add(billEntity.tableTimes)
            billMonthEntity.total += billEntity.total
            billMonthEntity.payOut += billEntity.payOut
            for (billSubEntity in billEntity.subList) {
                if (billSubEntity.payType == "人工") {
                    billMonthEntity.payOutForLabor += billSubEntity.payAmount.safeToDouble().absoluteValue
                }
                if (billSubEntity.payType == "水费" || billSubEntity.payType == "电费") {
                    billMonthEntity.payOutForWEG += billSubEntity.payAmount.safeToDouble().absoluteValue
                }
                if (billSubEntity.payType == "房租") {
                    billMonthEntity.payOutForRent += billSubEntity.payAmount.safeToDouble().absoluteValue
                }
            }
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

fun parseBillTotal(jsonData: String?): Float {
    jsonData?: return 0f
    return try {
        JSONObject(jsonData).optJSONObject("data")?.optDouble("total")?.toFloat()?: 0f
    } catch (e: Exception) {
        0f
    }
}