package com.godq.keepaccounts.constants

const val HOST = "http://150.158.55.208"


fun getShopListUrl(): String {
    return "$HOST/shop/list"
}

fun getBillListUrl(shopId: String): String {
    return "$HOST/bill/fetch?shop_id=$shopId"
}

fun getUpdateBillUrl(): String {
    return "$HOST/bill/update"
}

