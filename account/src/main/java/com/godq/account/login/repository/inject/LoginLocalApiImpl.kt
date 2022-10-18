package com.godq.account.login.repository.inject

import com.godq.account.AccountInfo
import com.godq.account.login.repository.LoginLocalDataSource
import com.lazylite.mod.config.ConfMgr

class LoginLocalApiImpl: LoginLocalDataSource.ILoginApi {

    override fun login(): AccountInfo {
        with(AccountInfo()) {
            set(ConfMgr.getStringValue("", "account_info", ""))
            return this
        }
    }

    override fun save(accountInfo: AccountInfo) {
        ConfMgr.setStringValue("", "account_info", accountInfo.toJson(), false)
    }
}