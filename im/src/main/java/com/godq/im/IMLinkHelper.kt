package com.godq.im

import android.app.Activity
import com.godq.accountsa.IAccountInfo
import com.godq.accountsa.IAccountService
import com.godq.ulda.IUploadService
import com.lazylite.bridge.router.ServiceImpl

object IMLinkHelper {

    private var accountService: IAccountService? = null
    private var uploadService: IUploadService? = null

    init {
        accountService = ServiceImpl.getInstance().getService(IAccountService::class.java.name) as? IAccountService
        uploadService = ServiceImpl.getInstance().getService(IUploadService::class.java.name) as? IUploadService
    }

    fun getAccountInfo(): IAccountInfo? {
        return accountService?.getAccountInfo()
    }

    fun upload(path: String, onUploadCallback: IUploadService.OnUploadCallback?) {
        uploadService?.upload(path, onUploadCallback)
    }

    fun chooseImage(fragmentOrActivity: Activity, onChooseImageCallback: IUploadService.OnChooseImageCallback?) {
        uploadService?.chooseImage(fragmentOrActivity, onChooseImageCallback)
    }

}