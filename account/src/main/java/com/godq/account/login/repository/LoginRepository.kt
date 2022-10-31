package com.godq.account.login.repository

import com.godq.account.AccountInfo
import com.godq.account.AccountCommonParamProvider
import com.godq.account.getUpdateUrl
import com.godq.accountsa.IAccountService
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
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

    fun updateAvatar(avatarUrl: String) {
        val json = JSONObject()
        json.putOpt("user_avatar", avatarUrl)
        val req = RequestInfo.newPost(getUpdateUrl(), mapOf("Content-Type" to "application/json"), json.toString().toByteArray())
        KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
            if (!it.isSuccessful) return@asyncPost
            //更新缓存
            accountInfo.mAvatarUrl = avatarUrl
            //本地持久化
            loginLocalDataSource.save(accountInfo)
            //全局广播登录消息
            MessageManager.getInstance().asyncNotify(
                IAccountService.IAccountObserver.EVENT_ID,
                object : MessageManager.Caller<IAccountService.IAccountObserver>() {
                    override fun call() {
                        ob.onUpdate()
                    }
                })
        }
    }

    fun logout() {
        accountInfo.reset()
        //本地持久化
        loginLocalDataSource.save(accountInfo)
        //全局广播登出消息
        MessageManager.getInstance().asyncNotify(
            IAccountService.IAccountObserver.EVENT_ID,
            object : MessageManager.Caller<IAccountService.IAccountObserver>() {
                override fun call() {
                    ob.onLogout()
                }
            })
    }
}