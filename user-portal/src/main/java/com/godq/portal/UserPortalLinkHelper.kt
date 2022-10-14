package com.godq.portal

import com.godq.accountsa.IAccountInfo
import com.godq.accountsa.IAccountService
import com.lazylite.bridge.router.ServiceImpl

object UserPortalLinkHelper {
    private var accountService: IAccountService? = null

    init {
        accountService = ServiceImpl.getInstance().getService(IAccountService::class.java.name) as? IAccountService
    }

    fun isLogin(): Boolean = accountService?.isLogin()?: false

    fun getAccountInfo(): IAccountInfo? = accountService?.getAccountInfo()
}