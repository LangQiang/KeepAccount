package com.godq.account

const val HOST = "http://150.158.55.208"

fun getRegisterUrl(): String {
    return "$HOST/account/register"
}