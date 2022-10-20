package com.godq.keepaccounts

import androidx.fragment.app.Fragment
import com.godq.accountsa.IAccountService
import com.godq.upa.IUserPortalService
import com.lazylite.bridge.router.ServiceImpl
import timber.log.Timber
import java.security.MessageDigest

object MainLinkHelper {

    private var userPortalService: IUserPortalService? = null
    private var accountService: IAccountService? = null

    init {
        userPortalService = ServiceImpl.getInstance().getService(IUserPortalService::class.java.name) as? IUserPortalService
        accountService = ServiceImpl.getInstance().getService(IAccountService::class.java.name) as? IAccountService
    }

    fun getUserHomeFragment(): Fragment? {
        return userPortalService?.getShopListFragment()
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

}