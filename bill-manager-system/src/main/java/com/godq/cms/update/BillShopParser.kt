package com.godq.keepaccounts.shop

import org.json.JSONObject

fun parseShopList(jsonData: String?): List<ShopEntity>? {
    jsonData ?: return null
    return try {
        val retList = ArrayList<ShopEntity>()
        val obj = JSONObject(jsonData)
        if (obj.optInt("code") != 200) {
            return null
        }
        val array = obj.optJSONArray("data") ?: return null

        for (i in 0 until array.length()) {
            val itemObj = array.optJSONObject(i) ?: continue
            retList.add(
                ShopEntity(
                    itemObj.optString("id"),
                    itemObj.optString("name"),
                    itemObj.optString("img"),
                    itemObj.optString("desc"),
                    itemObj.optString("addr"),
                    itemObj.optString("phone")
                )
            )
        }

        return retList
    } catch (e: Exception) {
        null
    }
}