package com.godq.portal.constants

const val HOST = "http://150.158.55.208"


fun getShopListUrl(): String {
    return "$HOST/shop/list"
}

fun getBillListUrl(shopId: String): String {
    return "$HOST/bill/fetch?shop_id=$shopId"
}

fun getHolidayStateUrl(startDate: String, endDate: String): String {
    return "$HOST/tool/holiday?start_date=$startDate&end_date=$endDate"
}

fun getUpdateBillUrl(): String {
    return "$HOST/bill/update"
}

