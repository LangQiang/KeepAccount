package com.godq.upload

import android.content.Context
import androidx.annotation.Nullable

import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider
import com.tencent.qcloud.core.auth.QCloudCredentialProvider
import com.tencent.cos.xml.CosXmlServiceConfig
import com.tencent.cos.xml.CosXmlSimpleService
import com.tencent.cos.xml.exception.CosXmlServiceException
import com.tencent.cos.xml.exception.CosXmlClientException
import com.tencent.cos.xml.model.CosXmlRequest
import com.tencent.cos.xml.model.CosXmlResult
import com.tencent.cos.xml.listener.CosXmlResultListener
import com.tencent.cos.xml.transfer.COSXMLUploadTask.COSXMLUploadTaskResult
import com.tencent.cos.xml.transfer.TransferManager
import com.tencent.cos.xml.transfer.TransferConfig

import timber.log.Timber
import java.io.File
import java.util.*


object COSManager {

    private var applicationContext: Context? = null

    private const val secretId = "AKIDCSm8XuHg7b9hCdyFSYeildUFuaUQ4mXJ" //永久密钥 secretId

    private const val secretKey = "tox9PfvASBxqjYNdoY5RaWEk3A4rW2MU" //永久密钥 secretKey

    // 存储桶所在地域简称，例如广州地区是 ap-guangzhou
    private const val region = "ap-beijing"

    // 初始化 COS Service，获取实例
    private var cosXmlService: CosXmlSimpleService? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
        // keyDuration 为请求中的密钥有效期，单位为秒
        val myCredentialProvider: QCloudCredentialProvider =
            ShortTimeCredentialProvider(secretId, secretKey, 300)
        val serviceConfig = CosXmlServiceConfig.Builder()
            .setRegion(region)
            .isHttps(true) // 使用 HTTPS 请求, 默认为 HTTP 请求
            .builder()
        cosXmlService = CosXmlSimpleService(applicationContext, serviceConfig, myCredentialProvider)
    }

    fun upload(path: String, onUploadResultCallback: OnUploadResultCallback?) {

        //限制文件大小
        with(File(path)) {
            if (length() > 20 * 1024 * 1024) {
                Timber.tag("upload").e("size over limit: ${length()}")
                onUploadResultCallback?.onResult(true, null, "不能上传大小超过超过20M的文件")
                return
            }
        }

        val transferConfig = TransferConfig.Builder().build()

        val transferManager = TransferManager(cosXmlService, transferConfig)

        // 存储桶名称，由bucketname-appid 组成，appid必须填入，可以在COS控制台查看存储桶名称。 https://console.cloud.tencent.com/cos5/bucket
        val bucket = "godq-1307306000"
        val cosPath = UUID.randomUUID().toString().replace(Regex("-"), "") //对象在存储桶中的位置标识符，即称对象键

        //本地文件的绝对路径
        val srcPath: String = path

        //若存在初始化分块上传的 UploadId，则赋值对应的 uploadId 值用于续传；否则，赋值 null
        val uploadId: String? = null

        // 上传文件
        val cosXmlUploadTask = transferManager.upload(
            bucket, cosPath,
            srcPath, uploadId
        )

        //设置上传进度回调
        cosXmlUploadTask.setCosXmlProgressListener { complete, target ->
            onUploadResultCallback?.onProgress(complete, target)
            Timber.tag("upload").e("Progress { complete: $complete  target: $target }")
        }

        //设置返回结果回调
        cosXmlUploadTask.setCosXmlResultListener(object : CosXmlResultListener {
            override fun onSuccess(request: CosXmlRequest, result: CosXmlResult) {
                onUploadResultCallback?.onResult(true, result.accessUrl, "success")
                val uploadResult = result as? COSXMLUploadTaskResult
                Timber.tag("upload").e("onSuccess ${uploadResult?.printResult()} ${result.accessUrl}")
            }

            // 如果您使用 kotlin 语言来调用，请注意回调方法中的异常是可空的，否则不会回调 onFail 方法，即：
            // clientException 的类型为 CosXmlClientException?，serviceException 的类型为 CosXmlServiceException?
            override fun onFail(request: CosXmlRequest,
                                @Nullable clientException: CosXmlClientException?,
                                @Nullable serviceException: CosXmlServiceException?) {
                if (clientException != null) {
                    Timber.tag("upload").e("clientException: ${clientException.message}")
                    onUploadResultCallback?.onResult(false, null, clientException.message ?: "clientException")
                    clientException.printStackTrace()
                } else {
                    Timber.tag("upload").e("serviceException: ${serviceException?.message}")
                    onUploadResultCallback?.onResult(false, null, serviceException?.message ?: "serviceException")
                    serviceException?.printStackTrace()
                }
            }
        })
        //设置任务状态回调, 可以查看任务过程
        cosXmlUploadTask.setTransferStateListener {
            Timber.tag("upload").e("TransferState ${it.name}")
        }

    }

    interface OnUploadResultCallback {
        fun onResult(success: Boolean, accessUrl: String?, errorMsg: String)
        fun onProgress(progress: Long, total: Long) {}
    }

}