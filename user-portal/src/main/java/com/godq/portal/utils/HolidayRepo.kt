package com.godq.portal.utils

import com.godq.portal.constants.getHolidayStateUrl
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

object HolidayRepo {

    //（工作日 ：0 法定节假日：1 休息日加班：2 休息日 ：3）

    const val STATE_WORK_DAY = 0
    const val STATE_LEGAL_HOLIDAY = 1
    const val STATE_WORK_DAY_FILL = 2
    const val STATE_REST_DAY = 3

    private val holidayList = ConcurrentHashMap<String, Int>()

    fun fetchHolidayStateList(startDate: String, endDate: String): Boolean {
        val response = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getHolidayStateUrl(startDate, endDate)))
        if (response?.isSuccessful == false) return false
        val data = response.dataToString()
        Timber.tag("shopppp").e(data)
        updateHolidayList(data)
        return true
    }

    fun getHolidayState(date: String): Int? {
        return holidayList[date]
    }

    private fun updateHolidayList(jsonStr: String) {
        try {
            JSONObject(jsonStr).optJSONArray("data")?.apply {
                for (i in 0 until length()) {
                    val itemObj = optJSONObject(i) ?: continue
                    holidayList[itemObj.optString("date")] = itemObj.optInt("code")
                }
            }
        } catch (e: Exception) {

        }
    }

}