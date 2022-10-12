package com.godq.upa

import androidx.fragment.app.Fragment

interface IUserPortalService {
    fun getShopListFragment(): Fragment
    fun getMineFragment(): Fragment
}