package com.product.product

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils

val HOST: String = UrlEntrustUtils.entrustHost("http://43.138.100.114", "http://43.138.100.114")

fun getMaterialListUrl(): String {
    return "$HOST/product/material/list"
}

fun getUploadMaterialUrl(): String {
    return "$HOST/product/material/update"
}
