package com.godq.account

import com.lazylite.mod.http.mgr.test.UrlEntrustUtils

val HOST: String = UrlEntrustUtils.entrustHost("http://43.138.100.114", "http://43.138.100.114")

fun getRegisterUrl(): String {
    return "$HOST/account/register"
}

fun getLoginUrl(): String {
    return "$HOST/account/login"
}

fun getUpdateUrl(): String {
    return "$HOST/account/update"
}