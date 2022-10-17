package com.godq.account

import com.godq.accountsa.IAccountInfo
import com.godq.accountsa.IAccountService

class AccountService : IAccountService{
    override fun isLogin(): Boolean {
        return RepositorySFactory.getLoginRepository().getAccountInfoCache().isLogin()
    }

    override fun getAccountInfo(): IAccountInfo {
        return RepositorySFactory.getLoginRepository().getAccountInfoCache()
    }


}