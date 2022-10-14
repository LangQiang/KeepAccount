package com.godq.account.login.repository.inject

import com.godq.account.AccountInfo
import com.godq.account.getLoginUrl
import com.godq.account.getRegisterUrl
import com.godq.account.login.repository.LoginInfo
import com.godq.account.login.repository.LoginRemoteDataSource
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import org.json.JSONObject
import timber.log.Timber

class LoginRemoteApiImpl: LoginRemoteDataSource.ILoginApi {

    override fun login(loginInfo: LoginInfo): AccountInfo? {
        val header = mapOf("Content-Type" to "application/json")
        val json = JSONObject()
        json.putOpt("account_name", loginInfo.accountName)
        json.putOpt("pass_word", loginInfo.password)
        val req = RequestInfo.newPost(getLoginUrl(), header, json.toString().toByteArray())

        KwHttpMgr.getInstance().kwHttpFetch.post(req)?.apply {
            if (!isSuccessful) return null
            val retData = dataToString()
            Timber.tag("account").d(retData)
            //{"code": 200, "msg": "success", "data": {"token": "96ace6adf2c14683aa3d35aea64eade6", "nick_name": "GOD-Q", "user_id": "49152011"}}
            try {
                val jsonObj = JSONObject(retData)
                if (jsonObj.optInt("code") != 200) return null
                jsonObj.optJSONObject("data")?.apply {
                    val accountInfo = AccountInfo()
                    accountInfo.mToken = optString("token")
                    accountInfo.mNickname = optString("nick_name")
                    accountInfo.mUserId = optString("user_id")
                    return accountInfo
                }
            } catch (ignore: Exception) {
            }
        }
        return null
    }
}