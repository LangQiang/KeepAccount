package com.godq.im.chatroom

import org.json.JSONArray
import org.json.JSONObject

fun parseHistoryList(jsonStr: String): List<MessageEntity> {
    val retList = ArrayList<MessageEntity>()
    return try {
        val jsonArr = JSONArray(jsonStr)
        for (i in 0 until jsonArr.length()) {
            val obj = jsonArr.optJSONObject(i) ?: continue

            val entity = MessageEntity(
                obj.optString("id"),
                obj.optString("user_id"),
                obj.optString("msg"),
                obj.optString("nick_name"),
                obj.optInt("msg_type"),
                obj.optString("user_avatar"),
                obj.optInt("pic_width"),
                obj.optInt("pic_height")
            )
            retList.add(entity)
        }
        retList
    } catch (e: Exception) {
        retList
    }
}

fun parseSingleMsg(jsonStr: String): MessageEntity? {
    return try {
        val obj = JSONObject(jsonStr)
        MessageEntity(
            obj.optString("id"),
            obj.optString("user_id"),
            obj.optString("msg"),
            obj.optString("nick_name"),
            obj.optInt("msg_type"),
            obj.optString("user_avatar"),
            obj.optInt("pic_width"),
            obj.optInt("pic_height")
        )
    } catch (e: Exception) {
        null
    }
}