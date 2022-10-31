package com.godq.portal

import android.app.Activity
import androidx.fragment.app.Fragment
import com.godq.accountsa.IAccountInfo
import com.godq.accountsa.IAccountService
import com.godq.ulda.IUploadService
import com.lazylite.bridge.router.ServiceImpl

object UserPortalLinkHelper {
    private var accountService: IAccountService? = null
    private var uploadService: IUploadService? = null

    init {
        accountService = ServiceImpl.getInstance().getService(IAccountService::class.java.name) as? IAccountService
        uploadService = ServiceImpl.getInstance().getService(IUploadService::class.java.name) as? IUploadService
    }

    fun isLogin(): Boolean = accountService?.isLogin()?: false

    fun logout() {
        accountService?.logout()
    }

    fun getAccountInfo(): IAccountInfo? = accountService?.getAccountInfo()

    fun upload(path: String, onUploadCallback: IUploadService.OnUploadCallback?) {
        uploadService?.upload(path, onUploadCallback)
    }

    fun chooseImage(fragmentOrActivity: Activity, onChooseImageCallback: IUploadService.OnChooseImageCallback?) {
        uploadService?.chooseImage(fragmentOrActivity, onChooseImageCallback)
    }

    fun updateAvatar(avatarUrl: String) {
        accountService?.updateAvatar(avatarUrl)
    }


}