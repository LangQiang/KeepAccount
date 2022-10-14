package com.godq.account.login.repository

import com.godq.account.AccountInfo

class LoginLocalDataSource(private val api: ILoginApi) {

    fun login(loginInfo: LoginInfo): AccountInfo = api.login(loginInfo)

    interface ILoginApi {
        fun login(loginInfo: LoginInfo): AccountInfo
    }
}