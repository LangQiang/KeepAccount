package com.godq.account

import com.godq.accountsa.IAccountInfo

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

    override fun isLogin(): Boolean = mUserId.isNotEmpty() && mToken.isNotEmpty()

}