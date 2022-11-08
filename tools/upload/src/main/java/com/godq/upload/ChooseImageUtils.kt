package com.godq.upload

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.godq.threadpool.ThreadPool
import com.godq.ulda.IUploadService
import com.lazylite.mod.App
import com.lazylite.mod.log.LogMgr
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.utils.KwFileUtils
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

var sOnChooseImageCallback : IUploadService.OnChooseImageCallback? = null

fun pickSystemImage(
    activity: Activity,
    onChooseImageCallback: IUploadService.OnChooseImageCallback?
): Boolean {
    sOnChooseImageCallback = onChooseImageCallback
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
    return try {
        activity.startActivityForResult(intent, 11001)
        true
    } catch (e: Exception) {
        false
    }
}

fun chooseImageOnResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode != 11001 || resultCode != Activity.RESULT_OK) return
    val activity = App.getMainActivity() ?: return
    val uri = data?.data?: return
    getAbsolutePath(activity, uri, object : OnCallback {
        override fun onResult(result: String?) {
            sOnChooseImageCallback?.onChoose(result)
            Timber.tag("choose").e(result)
        }
    })
}

private fun getAbsolutePath(context: Context?, data: Uri?, callback: OnCallback) {
    if (data == null || context == null) {
        callback.onResult("")
        return
    }
    val scheme = data.scheme

    if ("content" == scheme) {
        parsePathFromContent(
            context,
            data,
            callback
        )
    } else if ("file" == scheme) {
        callback.onResult(parsePathFromFile(data))
    }
}

/**
 * 区分版本
 */
@SuppressLint("ObsoleteSdkInt")
private fun parsePathFromContent(context: Context?, data: Uri?, callback: OnCallback) {
    if (context == null || data == null) {
        callback.onResult("")
        return
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // <4.4
        val result: String? = getDataColumn(context,data,null,null)
        callback.onResult(result)
        return
    }
    if (DocumentsContract.isDocumentUri(context, data) && Build.VERSION.SDK_INT < 29) {
        try {
            val authority = data.authority
            //isExternalStorageDocument
            if ("com.android.externalstorage.documents" == authority) {
                val result: String = getExternalStorageDocumentPath(data)
                callback.onResult(result)
                return
            }
            //isDownloadsDocument
            if ("com.android.providers.downloads.documents" == authority) {
                val result: String? = getDownloadsDocumentPath(context, data)
                callback.onResult(result)
                return
            }
            //isMediaDocument
            if ("com.android.providers.media.documents" == authority) {
                val result: String? = getMediaDocumentPath(context, data)
                callback.onResult(result)
                return
            }
        } catch (ignore: java.lang.Exception) {
        }
    } else if (Build.VERSION.SDK_INT >= 29) {
        ThreadPool.exec {
            val result: String? = uriToFileApiQ(context, data)
            MessageManager.getInstance().asyncRun(object : MessageManager.Runner() {
                override fun call() {
                    callback.onResult(result)
                }
            })
        }
    } else if (isQQBrowser(data)) {
        callback.onResult(getQQBrowserPath(data))
    } else if (isGooglePhotosUri(data)) {
        callback.onResult(data.lastPathSegment)
    } else {
        val fromContentResolver: String? = getDataColumn(context, data, null, null)
        if (TextUtils.isEmpty(fromContentResolver)) {
            val path = data.path
            ThreadPool.exec {
                var lastTemp: String? = tempPickFile(path)
                if (TextUtils.isEmpty(lastTemp)) {
                    lastTemp = uriToFileApiQ(context, data) //最后再来一遍
                }
                val finalLastTemp = lastTemp
                MessageManager.getInstance().asyncRun(object : MessageManager.Runner() {
                    override fun call() {
                        callback.onResult(finalLastTemp)
                    }
                })
            }
        } else {
            callback.onResult(fromContentResolver)
        }
    }
}

private fun tempPickFile(path: String?): String? {
    path?: return ""
    val file = File(path)
    return if (file.exists()) {
        path
    } else {
        try {
            val index = path.indexOf("/")
            if (index < 0) {
                return ""
            }
            tempPickFile(path.substring(index + 1))
        } catch (e: java.lang.Exception) {
            ""
        }
    }
}

private fun getQQBrowserPath(data: Uri): String {
    val path = data.path
    if (path == null || !path.startsWith("/QQBrowser")) {
        return ""
    }
    val fileDir = Environment.getExternalStorageDirectory()
    val file = File(fileDir, path.substring("/QQBrowser".length))
    return if (file.exists()) file.toString() else ""
}


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
private fun getMediaDocumentPath(context: Context, data: Uri): String? {
    val docId = DocumentsContract.getDocumentId(data)
    val split = docId.split(":").toTypedArray()
    val type = split[0]
    var contentUri: Uri? = null
    when (type) {
        "image" -> {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        "video" -> {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        "audio" -> {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
    }
    val selection = "_id=?"
    val selectionArgs = arrayOf(split[1])
    return getDataColumn(
        context,
        contentUri,
        selection,
        selectionArgs
    )
}

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
private fun getDownloadsDocumentPath(context: Context, data: Uri): String? {
    val id = DocumentsContract.getDocumentId(data) ?: return ""
    return if (id.startsWith("raw:")) {
        id.substring(4)
    } else {
        var finalId = id
        if (finalId.startsWith("msf:")) {
            finalId = finalId.substring(4)
        }
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"),
            finalId.toLong()
        )
        getDataColumn(
            context,
            contentUri,
            null,
            null
        )
    }
}

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
private fun getExternalStorageDocumentPath(data: Uri): String {
    val docId = DocumentsContract.getDocumentId(data)
    val split = docId.split(":").toTypedArray()
    val type = split[0]
    return if ("primary".equals(type, ignoreCase = true)) {
        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
    } else ""
}


/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context       The context.
 * @param uri           The Uri to query.
 * @param selection     (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    uri ?: return ""
    val column = MediaStore.Audio.AudioColumns.DATA
    val projection = arrayOf(
        column
    )
    try {
        context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            .use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val index =
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
                    return cursor.getString(index)
                }
            }
    } catch (ignore: java.lang.Exception) {
        LogMgr.e("chooseFile", ignore)
    }
    return ""
}

private fun parsePathFromFile(data: Uri): String? {
    return data.path
}

private fun isQQBrowser(data: Uri): Boolean {
    return "com.tencent.mtt.fileprovider" == data.authority
}

fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

/**
 * Android 10 以上适配
 * @param context
 * @param uri
 * @return
 */
private fun uriToFileApiQ(context: Context, uri: Uri): String? {
    var file: File? = null
    //android10以上转换
    if (uri.scheme == ContentResolver.SCHEME_FILE) {
        val path = uri.path ?: return null
        file = File(path)
    } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        //把文件复制到沙盒目录
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null) ?: return null
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val displayName = cursor.getString(index)
            try {
                val originName = if (TextUtils.isEmpty(displayName)) UUID.randomUUID()
                    .toString() + System.currentTimeMillis() else displayName
                val stream = contentResolver.openInputStream(uri)
                val cache = File(context.externalCacheDir?.absolutePath, originName)
                KwFileUtils.fileCopy(stream, cache)
                file = cache
                stream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        cursor.close()
    }
    return file?.absolutePath
}

interface OnCallback {
    fun onResult(result: String?)
}
