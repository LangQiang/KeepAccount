package com.godq.account

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils

val HOST: String = UrlEntrustUtils.entrustHost("http://49.232.151.23", "http://49.232.151.23")

fun getRegisterUrl(): String {
    return "$HOST/account/register"
}

fun getLoginUrl(): String {
    return "$HOST/account/login"
}

fun getUpdateUrl(): String {
    return "$HOST/account/update"
}