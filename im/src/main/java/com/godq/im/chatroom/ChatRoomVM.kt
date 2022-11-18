package com.godq.im.chatroom

import android.graphics.BitmapFactory
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.godq.im.IMLinkHelper
import com.godq.im.PublicChatRoomManager
import com.godq.im.R
import com.godq.ulda.IUploadService
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.StartParameter
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.utils.toast.KwToast
import com.lazylite.mod.widget.preview.PhotoPreviewConfig
import com.lazylite.mod.widget.preview.PhotoPreviewFragment
import timber.log.Timber
import java.lang.ref.WeakReference

class ChatRoomVM  : LifecycleEventObserver {

    val inputTextMsgUIState = ObservableField("")

    val imgSendLoadingUIState = ObservableField(false)

    val imgSendProgressUIState = ObservableField(0.0)

    var onDataCallback: ((historyData: List<MessageEntity>) -> Unit)? = null

    private val onReceiveMessageListener = object : PublicChatRoomManager.OnReceiveMessageListener {
        override fun onMsgReceive(msg: MessageEntity) {
            Timber.tag("chatroom").e(msg.toString())
            MessageManager.getInstance().asyncRun(object : MessageManager.Runner() {
                override fun call() {
                    val list = ArrayList<MessageEntity>()
                    list.add(msg)
                    onDataCallback?.invoke(list)
                }

            })
        }

    }

    fun loadHistory() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet("${PublicChatRoomManager.HOST}:8001/history/list")) {
            if (!it.isSuccessful) return@asyncGet
            onDataCallback?.invoke(parseHistoryList(it.dataToString()))
        }
    }

    fun sendTextMsg() {
        val sendMsg = inputTextMsgUIState.get()?: return
        if (sendMsg.isEmpty()) return
        Timber.tag("chatroom").e(sendMsg)
        PublicChatRoomManager.sendMsg(sendMsg)
        inputTextMsgUIState.set("")
    }

    fun onSendImgMsg() {
        val activity = App.getMainActivity()?: return
        IMLinkHelper.chooseImage(activity, object : IUploadService.OnChooseImageCallback {
            override fun onChoose(fileUri: String?) {
                upload(fileUri)
            }

        })
    }

    private fun upload(fileUri: String?) {
        if (fileUri.isNullOrEmpty()) {
            KwToast.show("发送失败")
            imgSendLoadingUIState.set(false)
            return
        }
        imgSendLoadingUIState.set(true)
//        val path = try {
//            Uri.parse(fileUri).path
//        } catch (e: Exception) {
//            imgSendLoadingUIState.set(false)
//            KwToast.show("发送失败")
//            null
//        } ?: return
        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeFile(fileUri, option)
        val width = option.outWidth
        val height = option.outHeight
        IMLinkHelper.upload(fileUri, object : IUploadService.OnUploadCallback {
            override fun onUpload(accessUrl: String?) {
                if (accessUrl.isNullOrEmpty()) {
                    KwToast.show("发送失败")
                    imgSendLoadingUIState.set(false)
                    return
                }
                PublicChatRoomManager.sendImgMsg(accessUrl, width, height)
                imgSendLoadingUIState.set(false)
            }

            override fun onProgress(progress: Long, total: Long) {
                super.onProgress(progress, total)
                if (total == 0L) return
                val progressD = progress.toDouble() / total
                imgSendProgressUIState.set(progressD)
                Timber.tag("chatroom").e("progress: $progressD")
            }

        })
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Timber.tag("chatroom").e(event.name)
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                PublicChatRoomManager.setOnReceiveMessageListener(onReceiveMessageListener)
            }
            Lifecycle.Event.ON_DESTROY -> {
                PublicChatRoomManager.removeOnReceiveMessageListener()
            }
            else -> {
                //ignore
            }
        }
    }

    fun onItemChildClick(messageEntity: MessageEntity?, view: View) {
        messageEntity?: return

        when (view.id) {
            R.id.other_iv, R.id.self_iv -> {
                val url = messageEntity.msg ?: return
                val config = PhotoPreviewConfig(WeakReference(view), url, false, 320, false)
                FragmentOperation.getInstance().showFullFragment(PhotoPreviewFragment.getInstance(config), StartParameter.Builder()
                    .withHideBottomLayer(false)
                    .build())
            }
            else -> {}
        }
    }
}