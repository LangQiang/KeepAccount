package com.godq.cms

import android.app.Activity
import com.godq.ulda.IUploadService
import com.lazylite.bridge.router.ServiceImpl

object ManagerLinkHelper {
    private var uploadService: IUploadService? = null

    init {
        uploadService = ServiceImpl.getInstance().getService(IUploadService::class.java.name) as? IUploadService
    }


    fun upload(path: String, onUploadCallback: IUploadService.OnUploadCallback?) {
        uploadService?.upload(path, onUploadCallback)
    }

    fun chooseImage(activity: Activity, onChooseImageCallback: IUploadService.OnChooseImageCallback?) {
        uploadService?.chooseImage(activity, onChooseImageCallback)
    }

}