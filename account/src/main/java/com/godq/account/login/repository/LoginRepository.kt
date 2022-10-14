package com.godq.account.login.repository

import com.godq.account.AccountInfo
import com.godq.accountsa.IAccountService
import com.lazylite.mod.messagemgr.MessageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepository (
    private val loginRemoteDataSource: LoginRemoteDataSource,
    private val loginLocalDataSource: LoginLocalDataSource
    ) {

    suspend fun login(loginInfo: LoginInfo): AccountInfo {
        val info = withContext(Dispatchers.IO) {
            loginRemoteDataSource.login(loginInfo)
//        loginLocalDataSource.login(loginInfo)
        }?.apply {
            MessageManager.getInstance().asyncNotify(IAccountService.IAccountObserver.EVENT_ID, object : MessageManager.Caller<IAccountService.IAccountObserver>(){
                override fun call() {
                    ob.onLogin()
                }
            })
        }
        return info?: AccountInfo()
     }
}