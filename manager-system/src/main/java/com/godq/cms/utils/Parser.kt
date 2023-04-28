package com.godq.cms.utils

import com.godq.cms.procure.ProcureEntity
import com.godq.cms.procure.detail.EquipmentEntity
import org.json.JSONObject


/**
 * @author  GodQ
 * @date  2023/4/25 5:48 下午
 */

fun parseProcureList(data: String?): List<ProcureEntity>? {
    data?: return null
    try {
        val jsonArr = JSONObject(data).optJSONArray("data")?: return null

        val retList = ArrayList<ProcureEntity>()

        for (i in 0 until jsonArr.length()) {
            jsonArr.optJSONObject(i)?.apply {
                val entity = ProcureEntity(
                    this.optString("procure_id"),
                    this.optString("procure_name"),
                    this.optString("procure_desc"),
                    this.optString("procure_notes"),
                    this.optInt("procure_state"),
                    this.optString("procure_created_time"),
                )
                retList.add(entity)
            }
        }
        return retList
    } catch (e: Exception) {
        return null
    }
}

fun parseEquipmentList(data: String?): List<EquipmentEntity>? {
    data?: return null
    try {
        val jsonArr = JSONObject(data).optJSONArray("data")?: return null

        val retList = ArrayList<EquipmentEntity>()

        for (i in 0 until jsonArr.length()) {
            jsonArr.optJSONObject(i)?.apply {
                val entity = EquipmentEntity(
                    this.optString("equipment_id"),
                    this.optString("procure_id"),
                    this.optString("equipment_name"),
                    this.optString("equipment_pic"),
                    this.optString("equipment_desc"),
                    this.optString("equipment_notes"),
                    this.optInt("equipment_state"),
                    this.optInt("equipment_count"),
                    this.optInt("equipment_per_price"),
                    this.optString("equipment_buy_channel"),
                    this.optString("equipment_complete_date"),
                    this.optString("equipment_purchaser"),
                    this.optString("equipment_created_time"),
                )
                retList.add(entity)
            }
        }
        return retList
    } catch (e: Exception) {
        return null
    }
}