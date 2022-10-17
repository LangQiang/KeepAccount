package com.godq.account.login.repository

import com.godq.account.AccountInfo
import com.godq.accountsa.IAccountService
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.messagemgr.MessageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LoginRepository (
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val loginLocalDataSource: LoginLocalDataSource
    ) {

    private val accountInfo = AccountInfo()

    fun autoLogin() {
        accountInfo.set(loginLocalDataSource.login())
        if (!accountInfo.isLogin()) return

        MessageManager.getInstance().asyncNotify(IAccountService.IAccountObserver.EVENT_ID,
            object : MessageManager.Caller<IAccountService.IAccountObserver>() {
                override fun call() {
                    ob.onLogin()
                }
            })
    }

    suspend fun login(loginInfo: LoginInfo): AccountInfo {
        val info = withContext(Dispatchers.IO) {
            loginRemoteDataSource.login(loginInfo)
        }?.apply {
            Timber.tag("account").e(this.mToken)
            accountInfo.set(this)
            if (!accountInfo.isLogin()) return@apply
            ConfMgr.setStringValue("", "account_info", accountInfo.toJson(), false)
            MessageManager.getInstance().asyncNotify(
                IAccountService.IAccountObserver.EVENT_ID,
                object : MessageManager.Caller<IAccountService.IAccountObserver>() {
                    override fun call() {
                        ob.onLogin()
                    }
                })
        }
        return info?: AccountInfo()
     }

    fun getAccountInfoCache(): AccountInfo = accountInfo
}