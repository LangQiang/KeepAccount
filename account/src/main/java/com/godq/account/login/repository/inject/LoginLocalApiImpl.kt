package com.godq.account.login.repository.inject

import com.godq.account.AccountInfo
import com.godq.account.getRegisterUrl
import com.godq.account.login.repository.LoginInfo
import com.godq.account.login.repository.LoginLocalDataSource
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import org.json.JSONObject
import timber.log.Timber

class LoginLocalApiImpl: LoginLocalDataSource.ILoginApi {

    override fun login(loginInfo: LoginInfo): AccountInfo {
        val header = mapOf("Content-Type" to "application/json")
        val json = JSONObject()
        json.putOpt("account_name", loginInfo.accountName)
        json.putOpt("pass_word", loginInfo.password)
        val req = RequestInfo.newPost(getRegisterUrl(), header, json.toString().toByteArray())

        KwHttpMgr.getInstance().kwHttpFetch.post(req)?.apply {
            if (!isSuccessful) return AccountInfo()
            return try {
                val retData = dataToString()
                Timber.tag("login").d(retData)
                JSONObject(retData).optInt("code") == 200
                AccountInfo()
            } catch (e: Exception) {
                AccountInfo()
            }
        }
        return AccountInfo()
    }
}