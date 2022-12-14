package com.godq.upload

import android.app.Activity
import android.content.Intent
import com.godq.ulda.IUploadService
import com.godq.upload.chooseimage.KSingChooseImageUtil
import com.lazylite.mod.App

class UploadServiceImpl: IUploadService {

    private var mChooseImage: KSingChooseImageUtil? = null

    init {
        COSManager.init(App.getInstance())
    }

    override fun chooseImage(activity: Activity, onChooseImageCallback: IUploadService.OnChooseImageCallback?) {
//        mChooseImage = KSingChooseImageUtil()
//        mChooseImage?.show(fragmentOrActivity) { imageUri ->
//            onChooseImageCallback?.onChoose(imageUri)
//            mChooseImage = null
//        }
        pickSystemImage(activity, onChooseImageCallback)
    }

    override fun chooseImageOnResultAssist(requestCode: Int, resultCode: Int, data: Intent?) {
        chooseImageOnResult(requestCode, resultCode, data)
//        mChooseImage?.onActivityResult(requestCode, resultCode, data)
    }

    override fun upload(path: String, onUploadCallback: IUploadService.OnUploadCallback?) {
        COSManager.upload(path, object : COSManager.OnUploadResultCallback {
            override fun onResult(success: Boolean, accessUrl: String?, errorMsg: String) {
                onUploadCallback?.onUpload(accessUrl)
            }

            override fun onProgress(progress: Long, total: Long) {
                super.onProgress(progress, total)
                onUploadCallback?.onProgress(progress, total)
            }

        })
    }

}