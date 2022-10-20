package com.godq.account

import com.godq.accountsa.IAccountInfo
import org.json.JSONObject

class AccountInfo: IAccountInfo {

    var mUserId: String = ""
    var mNickname: String = ""
    var mToken: String = ""

    override fun getUserId(): String {
        return mUserId
    }

    override fun getNickName(): String {
        return mNickname
    }

    override fun getToken(): String {
        return mToken
    }

    override fun isLogin(): Boolean = mUserId.isNotEmpty() && mToken.isNotEmpty()

    fun set(accountInfo: AccountInfo) {
        this.mToken = accountInfo.mToken
        this.mNickname = accountInfo.mNickname
        this.mUserId = accountInfo.mUserId
    }

    fun set(jsonStr: String) {
        if (jsonStr.isEmpty()) return
        try {
            with(JSONObject(jsonStr)) {
                mToken = optString("token")
                mNickname = optString("nick_name")
                mUserId = optString("user_id")
            }
        } catch (e: Exception) {

        }
    }

    fun toJson(): String {
        val json = JSONObject()
        json.putOpt("token", mToken)
        json.putOpt("nick_name", mNickname)
        json.putOpt("user_id", mUserId)
        return json.toString()
    }

}