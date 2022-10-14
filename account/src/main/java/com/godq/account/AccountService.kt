package com.godq.account

import com.godq.accountsa.IAccountInfo
import com.godq.accountsa.IAccountService

class AccountService : IAccountService{
    override fun isLogin(): Boolean {
        return false
    }

    override fun getAccountInfo(): IAccountInfo {
        return AccountInfo()
    }


}