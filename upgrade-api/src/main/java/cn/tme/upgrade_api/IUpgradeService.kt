package cn.tme.upgrade_api

import android.app.Activity


/**
 * @author  GodQ
 * @date  2023/3/24 3:30 下午
 */
interface IUpgradeService {
    fun check(activity: Activity?)
}