package com.godq.portal

import androidx.fragment.app.Fragment
import com.godq.portal.mine.MineFragment
import com.godq.portal.shop.ShopListFragment
import com.godq.upa.IUserPortalService

class UserPortalService: IUserPortalService {
    override fun getShopListFragment(): Fragment {
        return ShopListFragment()
    }

    override fun getMineFragment(): Fragment {
        return MineFragment()
    }
}