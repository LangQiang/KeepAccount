package com.godq.msa

import androidx.fragment.app.Fragment


/**
 * @author  GodQ
 * @date  2023/4/25 3:35 下午
 */
interface IManagerService {
    fun getMgrHomeFragment(): Fragment
    fun getProcureHomeFragment(): Fragment
    fun getProductHomeFragment(): Fragment
}