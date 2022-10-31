package com.godq.im.chatroom

import org.json.JSONArray
import org.json.JSONObject

fun parseHistoryList(jsonStr: String): List<MessageEntity> {
    val retList = ArrayList<MessageEntity>()
    return try {
        val jsonArr = JSONArray(jsonStr)
        for (i in 0 until jsonArr.length()) {
            val obj = jsonArr.optJSONObject(i) ?: continue
            val entity = MessageEntity()
            entity.userId = obj.optString("user_id")
            entity.msg = obj.optString("msg")
            entity.msgId = obj.optString("id")
            entity.userName = obj.optString("user_name")
            retList.add(entity)
        }
        retList
    } catch (e: Exception) {
        retList
    }
}

fun parseSingleMsg(jsonStr: String): MessageEntity? {
    return try {
        val jsonObj = JSONObject(jsonStr)
        val entity = MessageEntity()
        entity.userId = jsonObj.optString("user_id")
        entity.msg = jsonObj.optString("msg")
        entity.msgId = jsonObj.optString("id")
        entity.userName = jsonObj.optString("user_name")
        entity
    } catch (e: Exception) {
        null
    }
}