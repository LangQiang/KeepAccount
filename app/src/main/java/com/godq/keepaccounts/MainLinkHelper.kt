package com.godq.keepaccounts

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import cn.tme.upgrade_api.IUpgradeService
import com.godq.accountsa.IAccountService
import com.godq.msa.IManagerService
import com.godq.ulda.IUploadService
import com.godq.upa.IUserPortalService
import com.lazylite.bridge.router.ServiceImpl
import com.lq.product_api.IProductService
import timber.log.Timber
import java.security.MessageDigest

object MainLinkHelper {

    private var userPortalService: IUserPortalService? = null
    private var accountService: IAccountService? = null
    private var uploadService: IUploadService? = null
    private var mgrService: IManagerService? = null
    private var iUpgradeService: IUpgradeService? = null
    private var iProductService: IProductService? = null

    init {
        userPortalService = ServiceImpl.getInstance().getService(IUserPortalService::class.java.name) as? IUserPortalService
        accountService = ServiceImpl.getInstance().getService(IAccountService::class.java.name) as? IAccountService
        uploadService = ServiceImpl.getInstance().getService(IUploadService::class.java.name) as? IUploadService
        mgrService = ServiceImpl.getInstance().getService(IManagerService::class.java.name) as? IManagerService
        iUpgradeService = ServiceImpl.getInstance().getService(IUpgradeService::class.java.name) as? IUpgradeService
        iProductService = ServiceImpl.getInstance().getService(IProductService::class.java.name) as? IProductService
    }

    fun getUserHomeFragment(): Fragment? {
        return userPortalService?.getShopListFragment()
    }

    fun getProductHomeFragment(): Fragment? {
        return iProductService?.getProductHomeFragment()
    }

    fun getMgrFragment(): Fragment? {
        return mgrService?.getMgrHomeFragment()
    }

    fun getMineFragment(): Fragment? {
        return userPortalService?.getMineFragment()
    }

    fun getMD5(salt:String, data: String): String {
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(salt.toByteArray())
            messageDigest.update(data.toByteArray())
            val byteArray = messageDigest.digest()
            val md5StrBuff = StringBuffer()
            for (i in byteArray.indices) {
                if (Integer.toHexString(0xFF and byteArray[i].toInt()).length == 1) md5StrBuff.append(
                    "0"
                ).append(
                    Integer.toHexString(0xFF and byteArray[i].toInt())
                ) else md5StrBuff.append(
                    Integer.toHexString(
                        0xFF and byteArray[i]
                            .toInt()
                    )
                )
            }
            Timber.tag("md5").e(md5StrBuff.toString())
            return md5StrBuff.toString()
        } catch (e: Exception) {

        }
        return ""
    }

    fun getToken(): String = accountService?.getAccountInfo()?.getToken() ?: ""


    fun onResultCallbackForUploadChooseImage(requestCode: Int, resultCode: Int, data: Intent?) {
        uploadService?.chooseImageOnResultAssist(requestCode, resultCode, data)
    }

    fun isLogin(): Boolean {
        return accountService?.isLogin() == true
    }

    fun checkUpgrade(activity: Activity) {
        iUpgradeService?.check(activity)
    }

}