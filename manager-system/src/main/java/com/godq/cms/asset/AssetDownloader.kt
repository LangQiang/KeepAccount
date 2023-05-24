package com.godq.cms.asset

import com.lazylite.mod.http.mgr.HttpWrapper
import com.lazylite.mod.http.mgr.IKwHttpFetcher
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.DownReqInfo
import com.lazylite.mod.utils.KwDirs
import java.io.File
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


/**
 * @author  GodQ
 * @date  2023/5/22 6:42 下午
 */
object AssetDownloader {

    private val currentDownloadingIDs = HashSet<String>()

    private val downloadListeners = CopyOnWriteArrayList<DownloadListener>()

    fun addDownloadListener(downloadListener: DownloadListener) {
        downloadListeners.add(downloadListener)
    }

    fun removeDownloadListener(downloadListener: DownloadListener) {
        downloadListeners.remove(downloadListener)
    }

    fun download(entity: AssetEntity) {
        currentDownloadingIDs.add(entity.assetData.id)
        val savePath = getSavePath(entity.assetData).absolutePath
        val temp = "$savePath.temp"
        KwHttpMgr.getInstance().kwHttpFetch.asyncDownload(DownReqInfo(entity.assetData.url, temp, 0L), null, object : IKwHttpFetcher.DownloadListener{
            override fun onProgress(currentPos: Long, totalLength: Long, httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>?) {
                downloadListeners.forEach {
                    it.onProgress(entity.assetData.id, currentPos, totalLength)
                }
            }

            override fun onComplete(httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>?) {
                val tempFile = File(temp)
                if (tempFile.exists()) {
                    val newFile = File(savePath)
                    tempFile.renameTo(newFile)
                    downloadListeners.forEach {
                        it.onFinish(entity.assetData.id, savePath)
                    }
                    currentDownloadingIDs.remove(entity.assetData.id)
                } else {
                    downloadListeners.forEach {
                        it.onFinish(entity.assetData.id, null)
                    }
                    currentDownloadingIDs.remove(entity.assetData.id)
                }

            }

            override fun onCancel(httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>?) {
                downloadListeners.forEach {
                    it.onCancel(entity.assetData.id)
                }
                currentDownloadingIDs.remove(entity.assetData.id)
            }

            override fun onError(errorCode: Int, msg: String?, httpWrapper: HttpWrapper<IKwHttpFetcher.DownloadListener>?) {
                downloadListeners.forEach {
                    it.onFinish(entity.assetData.id, null)
                }
                currentDownloadingIDs.remove(entity.assetData.id)
            }
        })
    }

    fun getSavePath(entity: AssetData): File {
        val file = getSaveDir()
        if (!file.exists()) {
            file.mkdirs()
        }

        val filename = "${entity.title}_${entity.id}.${entity.format.lowercase()}"

        return File(file, filename)
    }

    private fun getSaveDir() = File(KwDirs.getKuWoRootPath().absolutePath + File.separator + "ShuangMenDong" + File.separator + "Assets")

    fun isDownloading(assetData: AssetData) = currentDownloadingIDs.contains(assetData.id)
    fun isComplete(assetData: AssetData) = getSavePath(assetData).exists()

    fun clearDownloadDir() {
        val folderQueue: Queue<File> = LinkedList()
        val folder = getSaveDir()
        if (!folder.exists() || !folder.isDirectory) return
        folderQueue.add(folder)
        while (!folderQueue.isEmpty()) {
            val currentFolder = folderQueue.poll()
            if (currentFolder != null) {
                val files = currentFolder.listFiles() ?: continue
                for (file in files) {
                    if (file.isDirectory) {
                        folderQueue.add(file)
                    } else {
                        file.delete()
                    }
                }
            }
        }
    }

    interface DownloadListener {
        fun onProgress(assetId: String, current: Long, total: Long) {}
        fun onFinish(assetId: String, savePath: String?)
        fun onCancel(assetId: String)
    }

}