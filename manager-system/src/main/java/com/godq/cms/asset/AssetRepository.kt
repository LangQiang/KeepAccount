package com.godq.cms.asset

import com.godq.cms.getAssetListUrl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


/**
 * @author  GodQ
 * @date  2023/5/16 5:51 下午
 */
class AssetRepository {

    suspend fun getAssets(): List<AssetData>? = withContext(Dispatchers.IO) {
        KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getAssetListUrl())).let {
            try {
                JSONObject(it.dataToString()).let { obj ->
                    val type = object : TypeToken<List<AssetData>>() {}.type
                    Gson().fromJson<List<AssetData>>(obj.optJSONArray("data")?.toString(), type)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}