package com.godq.cms

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils

val HOST: String = UrlEntrustUtils.entrustHost("http://49.232.151.23", "http://49.232.151.23")


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

fun getProcureListUrl(): String {
    return "$HOST/procure/list"
}

fun getCreateProcureUrl(): String {
    return "$HOST/procure/create"
}

fun getEquipmentListUrl(procureId: String?): String {
    return "$HOST/equipment/list/${procureId}"
}

fun getCreateEquipmentUrl(): String {
    return "$HOST/equipment/create"
}

fun getUpdateEquipmentUrl(): String {
    return "$HOST/equipment/update"
}

fun getAssetListUrl(): String {
    return "$HOST/asset/list"
}

fun getAddAssetUrl(): String {
    return "$HOST/asset/create"
}