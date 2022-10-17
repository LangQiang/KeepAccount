package com.godq.account

import android.content.Context
import android.util.Pair
import com.godq.accountsa.IAccountService
import com.lazylite.annotationlib.AutoInit
import com.lazylite.bridge.init.Init
import timber.log.Timber

@AutoInit
class AccountInit : Init() {

    override fun init(p0: Context?) {
        Timber.tag("test").e("AccountInit#init invoke!")
        RepositorySFactory.getLoginRepository().autoLogin()
    }

    override fun initAfterAgreeProtocol(p0: Context?) {
    }

    override fun getServicePair(): Pair<String, Any> {
        return Pair(IAccountService::class.java.name, AccountService())
    }
}