package com.godq.keepaccounts.billdetail

import org.json.JSONObject

fun parseBillDetailList(jsonData: String?): List<BillEntity>? {
    jsonData ?: return null
    return try {
        val retList = ArrayList<BillEntity>()
        val obj = JSONObject(jsonData)
        if (obj.optInt("code") != 200) {
            return null
        }
        val array = obj.optJSONArray("data") ?: return null

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
            retList.add(
                BillEntity(
                    BillEntity.TYPE_GENERAL,
                    false,
                    itemObj.optString("bill_date"),
                    itemObj.optDouble("bill_total"),
                    itemObj.optInt("bill_table_times"),
                    subList
                )
            )
        }

        return retList
    } catch (e: Exception) {
        null
    }
}