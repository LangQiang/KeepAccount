package com.godq.cms.asset

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.godq.cms.asset.add.AddAssetFragment
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.StartParameter
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.utils.toast.KwToast
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


/**
 * @author  GodQ
 * @date  2023/5/16 5:39 下午
 */
class AssetViewModel: ViewModel(), LifecycleEventObserver {

    private val repository = AssetRepository()

    val showFullPicUrl = mutableStateOf("")

    val assetList = mutableStateListOf<AssetEntity>()

    val state = mutableStateOf(false)

    private val currentItemInDownloading = HashMap<String, AssetEntity>()

    private val assetEvent = object : IAssetEvent {
        override fun onAddAsset() {
            loadAssets()
        }
    }

    private val downloadListener = object : AssetDownloader.DownloadListener {

        override fun onProgress(assetId: String, current: Long, total: Long) {
            val currentItem = currentItemInDownloading[assetId] ?: return
            val percent = current.toFloat() / total
            val lastPercent = try { currentItem.downloadState.value.toFloat() } catch (e: Exception) { 0f }
            if (percent - lastPercent < 0.01) {
                return
            }
            currentItemInDownloading[assetId]?.downloadState?.value = String.format("%.2f", percent)
        }

        override fun onFinish(assetId: String, savePath: String?) {
            if (savePath == null) {
                currentItemInDownloading[assetId]?.downloadState?.value = "下载"
            } else {
                currentItemInDownloading[assetId]?.downloadState?.value = "查看"
                Timber.tag("AssetViewModel").e("savePath: $savePath")
            }
        }

        override fun onCancel(assetId: String) {
            currentItemInDownloading[assetId]?.downloadState?.value = "下载"
        }

    }

    init {
        loadAssets()
    }

    private fun loadAssets() {
        viewModelScope.launch {
            assetList.clear()
            repository.getAssets()?.forEach {
                val entity = AssetEntity(it)
                initEntityState(entity)
                assetList.add(entity)
            }
        }
    }

    private fun initEntityState(entity: AssetEntity) {
        if (AssetDownloader.isDownloading(entity.assetData)) {
            entity.downloadState.value = "下载中"
            currentItemInDownloading[entity.assetData.id] = entity
        } else if (AssetDownloader.isComplete(entity.assetData)) {
            entity.downloadState.value = "查看"
        } else {
            entity.downloadState.value = "下载"
        }
    }

    fun onMenuClick() {
        FragmentOperation.getInstance().showFullFragment(AddAssetFragment(),
            StartParameter.Builder().withHideBottomLayer(false).build()
        )
//        AssetDownloader.clearDownloadDir()
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Timber.tag("AssetViewModel").e("Lifecycle.Event: ${event.name}")
        when (event) {
            Lifecycle.Event.ON_START -> {
                MessageManager.getInstance().attachMessage(IAssetEvent.EVENT_ID, assetEvent)
                AssetDownloader.addDownloadListener(downloadListener)
            }
            Lifecycle.Event.ON_STOP -> {
                MessageManager.getInstance().detachMessage(IAssetEvent.EVENT_ID, assetEvent)
                AssetDownloader.removeDownloadListener(downloadListener)
            }
            else -> {

            }
        }
    }

    fun onItemClick(assetEntity: AssetEntity) {
        when (assetEntity.assetData.format) {
            AssetData.FORMAT_PDF -> {
                when (assetEntity.downloadState.value) {
                    "下载" -> {
                        currentItemInDownloading[assetEntity.assetData.id] = assetEntity
                        AssetDownloader.download(entity = assetEntity)
                    }
                    "查看" -> {
                        App.getMainActivity()?.also { openPdfFile(it, AssetDownloader.getSavePath(assetEntity.assetData)) }
                    }
                    else -> {}
                }
            }
            AssetData.FORMAT_PIC -> {
                showFullPicUrl.value = assetEntity.assetData.url
            }
            else -> {}
        }

    }

    private fun openPdfFile(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "com.godq.keepaccounts.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // 处理打开 PDF 文件失败的情况
            KwToast.show("无法打开 PDF 文件")
        }
    }

    fun onFullPicClick() {
        showFullPicUrl.value = ""
    }

}