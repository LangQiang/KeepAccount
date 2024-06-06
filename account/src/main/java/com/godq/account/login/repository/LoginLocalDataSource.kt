package com.godq.account.login.repository

import com.godq.account.AccountInfo
import com.godq.accountsa.IAccountInfo

class LoginLocalDataSource(private val api: ILoginApi) {

    fun login(): AccountInfo = api.login()

    fun save(accountInfo: IAccountInfo) = api.save(accountInfo)

    interface ILoginApi {
        fun login(): AccountInfo
        fun save(accountInfo: IAccountInfo)
    }
}