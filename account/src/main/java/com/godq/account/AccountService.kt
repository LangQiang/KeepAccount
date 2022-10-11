package com.godq.account

import com.godq.accountsa.IAccountService
import timber.log.Timber

class AccountService : IAccountService{
    override fun test() {
        Timber.tag("test").e("IAccountService#test invoke!")
    }

}