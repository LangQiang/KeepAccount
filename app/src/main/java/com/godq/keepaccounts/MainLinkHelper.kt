package com.godq.keepaccounts

import androidx.fragment.app.Fragment
import com.godq.cms.BillMgrHomeFragment
import com.godq.portal.shop.ShopListFragment

object MainLinkHelper {

    fun getUserHomeFragment(): Fragment {
        return ShopListFragment()
    }

    fun getCMSHomeFragment(): Fragment {
        return BillMgrHomeFragment()
    }

}