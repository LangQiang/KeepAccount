package com.godq.account.login.repository

import com.godq.account.AccountInfo

class LoginLocalDataSource(private val api: ILoginApi) {

    fun login(): AccountInfo = api.login()

    fun save(accountInfo: AccountInfo) = api.save(accountInfo)

    interface ILoginApi {
        fun login(): AccountInfo
        fun save(accountInfo: AccountInfo)
    }
}