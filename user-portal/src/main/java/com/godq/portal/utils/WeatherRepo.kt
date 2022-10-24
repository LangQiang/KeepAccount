package com.godq.portal.utils

import com.godq.portal.constants.getWeatherUrl
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

object WeatherRepo {

    // xue、lei、shachen、wu、bingbao、yun、yu、yin、qing

    const val WEATHER_XUE = "xue"
    const val WEATHER_LEI = "lei"
    const val WEATHER_SHA_CHEN = "shachen"
    const val WEATHER_WU = "wu"
    const val WEATHER_BING_BAO = "bingbao"
    const val WEATHER_YUN = "yun"
    const val WEATHER_YU = "yu"
    const val WEATHER_YIN = "yin"
    const val WEATHER_QING = "qing"

    private val weatherList = ConcurrentHashMap<String, String>()

    fun fetchHolidayStateList(startDate: String, endDate: String): Boolean {
        val response = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getWeatherUrl(startDate, endDate)))
        if (response?.isSuccessful == false) return false
        val data = response.dataToString()
        Timber.tag("shopppp").e(data)
        updateWeatherList(data)
        return true
    }

    fun getWeather(date: String): String? {
        return weatherList[date]
    }

    private fun updateWeatherList(jsonStr: String) {
        try {
            JSONObject(jsonStr).optJSONArray("data")?.apply {
                for (i in 0 until length()) {
                    val itemObj = optJSONObject(i) ?: continue
                    val hourWeatherArr = itemObj.optJSONArray("hour_list") ?: continue
                    if (hourWeatherArr.length() == 0) continue
                    weatherList[itemObj.optString("date")] = hourWeatherArr.optJSONObject(0).optString("weather_condition")
                }
            }
        } catch (e: Exception) {

        }
    }

}