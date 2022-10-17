package com.godq.account.register.inject

import com.godq.account.getRegisterUrl
import com.godq.account.register.repository.RegisterInfo
import com.godq.account.register.repository.RegisterRemoteDataSource
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import org.json.JSONObject
import timber.log.Timber

class RegisterApiImpl: RegisterRemoteDataSource.IRegisterApi {

    override fun register(registerInfo: RegisterInfo): Boolean {
        val header = mapOf("Content-Type" to "application/json")
        val json = JSONObject()
        json.putOpt("account_name", registerInfo.accountName)
        json.putOpt("pass_word", registerInfo.password)
        json.putOpt("nick_name", registerInfo.nickName)
        val req = RequestInfo.newPost(getRegisterUrl(), header, json.toString().toByteArray())

        KwHttpMgr.getInstance().kwHttpFetch.post(req)?.apply {
            if (!isSuccessful) return false
            return try {
                val retData = dataToString()
                Timber.tag("account").e(retData)
                JSONObject(retData).optInt("code") == 200
            } catch (e: Exception) {
                false
            }
        }
        return false
    }
}