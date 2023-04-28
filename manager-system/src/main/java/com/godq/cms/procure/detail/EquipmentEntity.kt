package com.godq.cms.procure.detail


/**
 * @author  GodQ
 * @date  2023/4/25 6:28 下午
 */
data class EquipmentEntity(
    val equipmentId: String,
    val procure_id: String,
    val equipment_name: String,
    val equipment_pic: String,
    val equipment_desc: String,
    val equipment_notes: String,
    val equipment_state: Int,
    val equipment_count: Int,
    val equipment_per_price: Int,
    val equipment_buy_channel: String,
    val equipment_complete_date: String,
    val equipment_purchaser: String,
    val equipment_created_time: String,
) {
    companion object {
        const val STATE_TODO = 0
        const val STATE_DISCARD = 1
        const val STATE_COMPLETE = 2
    }
}