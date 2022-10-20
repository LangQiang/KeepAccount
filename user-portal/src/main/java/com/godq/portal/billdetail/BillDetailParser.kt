package com.godq.portal.billdetail

import android.text.TextUtils
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
                    subList
                )
            )
        }

        return retList
    } catch (e: Exception) {
        null
    }
}