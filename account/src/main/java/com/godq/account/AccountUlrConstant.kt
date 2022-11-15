package com.godq.account

const val HOST = "http://43.138.100.114"

fun getRegisterUrl(): String {
    return "$HOST/account/register"
}

fun getLoginUrl(): String {
    return "$HOST/account/login"
}

fun getUpdateUrl(): String {
    return "$HOST/account/update"
}