package com.godq.cms

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils

val HOST: String = UrlEntrustUtils.entrustHost("http://43.138.100.114", "http://43.138.100.114")


fun getShopListUrl(): String {
    return "$HOST/shop/list"
}

fun getBillListUrl(shopId: String): String {
    return "$HOST/bill/fetch?shop_id=$shopId"
}

fun getUpdateBillUrl(): String {
    return "$HOST/bill/update"
}

fun deleteBillRecordUrl(): String {
    return "$HOST/bill/delete"
}