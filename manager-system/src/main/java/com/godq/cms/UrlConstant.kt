package com.godq.cms

const val HOST = "http://43.138.100.114"


fun getShopListUrl(): String {
    return "$HOST/shop/list"
}

fun getBillListUrl(shopId: String): String {
    return "$HOST/bill/fetch?shop_id=$shopId"
}

fun getUpdateBillUrl(): String {
    return "$HOST/bill/update"
}