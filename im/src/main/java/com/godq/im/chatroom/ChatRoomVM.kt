package com.godq.im.chatroom

import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.godq.im.PublicChatRoomManager
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import timber.log.Timber

class ChatRoomVM  : LifecycleEventObserver {

    val msg = ObservableField("")

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
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet("http://43.138.100.114:8001/history/list")) {
            if (!it.isSuccessful) return@asyncGet
            onDataCallback?.invoke(parseHistoryList(it.dataToString()))
        }
    }

    fun sendMsg() {
        val sendMsg = msg.get()?: return
        Timber.tag("chatroom").e(sendMsg)
        PublicChatRoomManager.sendMsg(sendMsg)
        msg.set("")
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
}