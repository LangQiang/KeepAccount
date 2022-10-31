package com.godq.account

const val HOST = "http://150.158.55.208"

fun getRegisterUrl(): String {
    return "$HOST/account/register"
}

fun getLoginUrl(): String {
    return "$HOST/account/login"
}

fun getUpdateUrl(): String {
    return "$HOST/account/update"
}