package com.godq.im.chatroom

import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.godq.im.IMManager
import com.lazylite.mod.App
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.utils.SoftKeyboardHelper
import timber.log.Timber

class ChatRoomVM  : LifecycleEventObserver {

    val msg = ObservableField("")

    var onDataCallback: ((historyData: List<MessageEntity>) -> Unit)? = null

    private val onReceiveMessageListener = object : IMManager.OnReceiveMessageListener {
        override fun onMsgReceive(msg: String) {
            Timber.tag("chatroom").e(msg)
            onDataCallback?.invoke(parseSingleList(msg))
        }

    }

    fun loadHistory() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet("http://43.138.100.114:8001/history/list")) {
            if (!it.isSuccessful) return@asyncGet
            onDataCallback?.invoke(parseHistoryList(it.dataToString()))
        }
    }

    fun sendMsg() {
        val sendMsg = msg.get()?: return
        Timber.tag("chatroom").e(sendMsg)
        IMManager.sendMsg(sendMsg)
        msg.set("")
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Timber.tag("chatroom").e(event.name)
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                IMManager.addOnReceiveMessageListener(onReceiveMessageListener)
            }
            Lifecycle.Event.ON_DESTROY -> {
                IMManager.removeOnReceiveMessageListener(onReceiveMessageListener)
            }
            else -> {
                //ignore
            }
        }
    }
}