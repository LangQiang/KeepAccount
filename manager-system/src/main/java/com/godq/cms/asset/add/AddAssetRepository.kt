package com.godq.cms.asset.add

import com.godq.cms.ManagerLinkHelper
import com.godq.cms.asset.AssetData
import com.godq.cms.getAddAssetUrl
import com.godq.ulda.IUploadService
import com.google.gson.Gson
import com.lazylite.mod.App
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * @author  GodQ
 * @date  2023/5/18 6:17 下午
 */
class AddAssetRepository {

    suspend fun getFile(): String? {
        return suspendCoroutine { continuation ->
            val activity = App.getMainActivity()
            if (activity == null) {
                continuation.resume("")
                return@suspendCoroutine
            }
            ManagerLinkHelper.chooseFile(activity, object : IUploadService.OnChooseImageCallback {
                override fun onChoose(fileUri: String?) {
                    continuation.resume(fileUri?: "")
                }

                override fun onCancel() {
                    continuation.resume(null)
                }
            })
        }
    }

    suspend fun upload(filePath: String, progressCallback: (progress: Float) -> Unit): String? {
        return suspendCancellableCoroutine { continuation ->
            val task = ManagerLinkHelper.upload(filePath, object : IUploadService.OnUploadCallback{
                override fun onUpload(accessUrl: String?) {
                    continuation.resume(accessUrl)
                }

                override fun onProgress(progress: Long, total: Long) {

                    if (total == 0L) {
                        progressCallback(0f)
                    } else {
                        progressCallback((progress.toFloat() / total))
                    }
                }

            })
            continuation.invokeOnCancellation {
                task?.cancel()
            }
        }
    }

    suspend fun addAsset(assetData: AssetData) = withContext(Dispatchers.IO) {
        val json = Gson().toJson(assetData)
        val req = RequestInfo.newPost(getAddAssetUrl(), mapOf("Content-Type" to "application/json"), json.toString().toByteArray())
        val response = KwHttpMgr.getInstance().kwHttpFetch.post(req)
        Timber.tag("AddAssetViewModel").e("final create ret: ${response.dataToString()}")
        response.isSuccessful
    }

}