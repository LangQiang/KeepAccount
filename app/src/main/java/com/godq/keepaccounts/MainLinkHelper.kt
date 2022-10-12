package com.godq.keepaccounts

import androidx.fragment.app.Fragment
import com.godq.cms.BillMgrHomeFragment
import com.godq.portal.shop.ShopListFragment
import com.godq.upa.IUserPortalService
import com.lazylite.bridge.router.ServiceImpl

object MainLinkHelper {

    private var userPortalService: IUserPortalService? = null

    init {
        userPortalService = ServiceImpl.getInstance().getService(IUserPortalService::class.java.name) as? IUserPortalService
    }

    fun getUserHomeFragment(): Fragment? {
        return userPortalService?.getShopListFragment()
    }

    fun getMineFragment(): Fragment? {
        return userPortalService?.getMineFragment()
    }

}