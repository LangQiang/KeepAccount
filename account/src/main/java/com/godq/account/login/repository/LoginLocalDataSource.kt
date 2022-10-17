package com.godq.account.login.repository

import com.godq.account.AccountInfo

class LoginLocalDataSource(private val api: ILoginApi) {

    fun login(): AccountInfo = api.login()

    interface ILoginApi {
        fun login(): AccountInfo
    }
}