package com.godq.account

import com.godq.accountsa.IAccountInfo
import org.json.JSONObject

class AccountInfo: IAccountInfo {

    var mUserId: String = ""
    var mNickname: String = ""
    var mToken: String = ""
    var mAvatarUrl: String = ""

    override fun getUserId(): String {
        return mUserId
    }

    override fun getNickName(): String {
        return mNickname
    }

    override fun getToken(): String {
        return mToken
    }

    override fun getAvatarUrl(): String {
        return mAvatarUrl
    }

    override fun isLogin(): Boolean = mUserId.isNotEmpty() && mToken.isNotEmpty()
    override fun updateInfo(nickName: String?, avatarUrl: String?) {
        if (nickName != null) {
            this.mNickname = nickName
        }
        if (avatarUrl != null) {
            this.mAvatarUrl = avatarUrl
        }
    }

    fun set(accountInfo: AccountInfo) {
        this.mToken = accountInfo.mToken
        this.mNickname = accountInfo.mNickname
        this.mUserId = accountInfo.mUserId
        this.mAvatarUrl = accountInfo.mAvatarUrl
    }

    fun set(jsonStr: String) {
        if (jsonStr.isEmpty()) return
        try {
            with(JSONObject(jsonStr)) {
                mToken = optString("token")
                mNickname = optString("nick_name")
                mUserId = optString("user_id")
                mAvatarUrl = optString("user_avatar")
            }
        } catch (e: Exception) {
            //
        }
    }

    override fun toJson(): String {
        val json = JSONObject()
        json.putOpt("token", mToken)
        json.putOpt("nick_name", mNickname)
        json.putOpt("user_id", mUserId)
        json.putOpt("user_avatar", mAvatarUrl)
        return json.toString()
    }

    fun reset() {
        mUserId = ""
        mNickname = ""
        mToken = ""
        mAvatarUrl = ""
    }

}