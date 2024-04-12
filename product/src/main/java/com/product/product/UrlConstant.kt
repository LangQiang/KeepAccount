package com.product.product

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils

val HOST: String = UrlEntrustUtils.entrustHost("http://49.232.151.23", "http://49.232.151.23")

fun getMaterialListUrl(): String {
    return "$HOST/product/material/list"
}

fun getUploadMaterialUrl(): String {
    return "$HOST/product/material/update"
}
