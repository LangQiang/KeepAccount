package com.godq.cms.asset.add

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godq.cms.asset.AssetData
import com.godq.cms.asset.IAssetEvent
import com.godq.cms.common.globalCoroutineExceptionHandler
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.utils.toast.KwToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/5/18 4:41 下午
 */
class AddAssetViewModel: ViewModel() {

    //repo
    private val repo = AddAssetRepository()

    //coroutine
    private var requestJob: Job? = null

    //ui state
    val progress = mutableStateOf(0f)
    val uploadState = mutableStateOf(UPLOAD_STATE_PREPARE)
    val title = mutableStateOf("")
    val belong = mutableStateOf("")
    val format = mutableStateOf("")
    val url = mutableStateOf("")
    val extra = mutableStateOf("")


    companion object {
        const val UPLOAD_STATE_PREPARE = 0
        const val UPLOAD_STATE_UPLOADING = 1
        const val UPLOAD_STATE_FINISH_SUC = 2
        const val UPLOAD_STATE_FINISH_FAIL = 3
        const val UPLOAD_STATE_CANCEL = 4
    }

    fun onUploadClick() {
        if (uploadState.value == UPLOAD_STATE_UPLOADING) {
            cancel()
        } else {
            upload()
        }
    }

    private fun cancel() {
        //取消
        setState(0f, UPLOAD_STATE_PREPARE)
        requestJob?.cancel()
    }

    private fun upload() {
        //上传
        setState(0f, UPLOAD_STATE_UPLOADING)
        requestJob = viewModelScope.launch(globalCoroutineExceptionHandler) {

            //获取文件
            val filePath = repo.getFile()
            Timber.tag("AddAssetViewModel").e("url: $filePath")
            if (filePath == null) {
                setState(0f, UPLOAD_STATE_CANCEL)
                return@launch
            } else if (filePath.isEmpty()) {
                setState(0f, UPLOAD_STATE_FINISH_FAIL)
                return@launch
            }

            //上传
            val uploadUrl = repo.upload(filePath) { progress ->
                Timber.tag("AddAssetViewModel").e("progress: $progress")
                setState(progress, UPLOAD_STATE_UPLOADING)
            }
            if (uploadUrl != null) {
                url.value = uploadUrl
                setState(1f, UPLOAD_STATE_FINISH_SUC)
            } else {
                setState(0f, UPLOAD_STATE_FINISH_FAIL)
            }
            Timber.tag("AddAssetViewModel").e("url: $uploadUrl")
        }
    }


    private fun setState(progressValue: Float, state: Int) {
        progress.value = progressValue
        uploadState.value = state
    }

    fun onCommitClick() {
        if (title.value.isEmpty() || format.value.isEmpty() || url.value.isEmpty()) {
            KwToast.show("信息不完整，title、format和url均不能为空")
            return
        }
        if (uploadState.value != UPLOAD_STATE_FINISH_SUC) {
            KwToast.show("文件上传失败，请重新选择上传")
            return
        }
        Timber.tag("AddAssetViewModel").e("title:${title.value} belong:${belong.value} format:${format.value} url:${url.value} extra:${extra.value}")
        viewModelScope.launch {
            val ret = repo.addAsset(AssetData(
                title = title.value,
                belong = belong.value,
                format = format.value,
                url = url.value,
                extra = extra.value,
            ))
            if (ret) {
                notifyAssetAdded()
                FragmentOperation.getInstance().close()
            } else {
                KwToast.show("添加失败")
                setState(0f, UPLOAD_STATE_FINISH_FAIL)
            }
        }

    }

    private fun notifyAssetAdded() {
        MessageManager.getInstance().asyncNotify(IAssetEvent.EVENT_ID, object : MessageManager.Caller<IAssetEvent>() {
            override fun call() {
                ob.onAddAsset()
            }
        })
    }

}