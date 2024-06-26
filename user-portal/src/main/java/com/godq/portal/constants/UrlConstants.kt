package com.godq.portal.constants

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils

val HOST: String = UrlEntrustUtils.entrustHost("http://49.232.151.23", "http://49.232.151.23")


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

fun getBillTotal(shopId: String?, typeName: String? = "", subTypeName: String? = "", startDate: String = "2023-08-21", endDate: String = "2025-01-01"): String {
    return "$HOST/bill/total?bill_shop_id=$shopId&type_name=$typeName&sub_type_name=$subTypeName&start_date=$startDate&end_date=$endDate"
}

fun getUpdateUrl(): String {
    return "$HOST/account/update"
}


fun getShopLatestDataListUrl(shopIds: Array<String>): String {
    return "$HOST/shop/list/latest?shop_ids=${shopIds.joinToString(separator = ",")}"
}

