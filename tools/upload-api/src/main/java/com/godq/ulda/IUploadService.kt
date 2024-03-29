package com.godq.ulda

import android.app.Activity
import android.content.Intent

interface IUploadService {

    fun chooseImage(activity: Activity, onChooseImageCallback: OnChooseImageCallback?)
    fun chooseFile(activity: Activity, onChooseImageCallback: OnChooseImageCallback?)

    /**
     * 要使得chooseImage接口回调生效 务必要在activity的onActivityResult中调用这个接口
     * */
    fun chooseImageOnResultAssist(requestCode: Int, resultCode: Int, data: Intent?)

    fun upload(path: String, onUploadCallback: OnUploadCallback?): IUploadTask?

    interface OnChooseImageCallback {
        fun onChoose(fileUri: String?)
        fun onCancel()
    }

    interface OnUploadCallback {
        fun onUpload(accessUrl: String?)
        fun onProgress(progress: Long, total: Long) {}
    }

    interface IUploadTask {
        fun cancel()
    }
}