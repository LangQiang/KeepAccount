package com.godq.portal.constants

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils

val HOST: String = UrlEntrustUtils.entrustHost("http://43.138.100.114", "http://43.138.100.114")


fun getShopListUrl(): String {
    return "$HOST/shop/list"
}

fun getBillListUrl(shopId: String): String {
    return "$HOST/bill/fetch?shop_id=$shopId"
}

fun getHolidayStateUrl(startDate: String, endDate: String): String {
    return "$HOST/tool/holiday?start_date=$startDate&end_date=$endDate"
}

fun getWeatherUrl(startDate: String, endDate: String): String {
    return "$HOST/tool/weather?start_date=$startDate&end_date=$endDate"
}

fun getUpdateBillUrl(): String {
    return "$HOST/bill/update"
}

