package com.godq.account.login.repository

import com.godq.account.AccountInfo
import com.godq.account.AccountCommonParamProvider
import com.godq.accountsa.IAccountService
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
        //更新内存中账号信息
        accountInfo.set(loginLocalDataSource.login())
        if (!accountInfo.isLogin()) return
        //设置通用header
        AccountCommonParamProvider.updateHeader("token", accountInfo.mToken)
        //设置通用queryParam
        AccountCommonParamProvider.updateQueryParam("user_id", accountInfo.mUserId)
        //全局广播登录消息
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
            //更新内存中账号信息
            accountInfo.set(this)
            if (!accountInfo.isLogin()) return@apply
            //设置通用header
            AccountCommonParamProvider.updateHeader("token", accountInfo.mToken)
            //设置通用queryParam
            AccountCommonParamProvider.updateQueryParam("user_id", accountInfo.mUserId)
            //本地持久化
            loginLocalDataSource.save(accountInfo)
            //全局广播登录消息
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