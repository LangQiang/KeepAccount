package com.godq.im

import com.godq.accountsa.IAccountService
import com.godq.im.chatroom.MessageEntity
import com.godq.im.chatroom.parseSingleMsg
import com.lazylite.bridge.router.ServiceImpl
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import timber.log.Timber
import java.lang.ref.WeakReference
import java.net.URISyntaxException

object PublicChatRoomManager {

    private var connected = false

    private var mSocket: Socket? = null

    private var lastReadMsgId: String? = null // none 表示没有未读  _include 表示包含本条id

    private var unReadMsgCount: Int = 0

    private var receiveMessageListener: OnReceiveMessageListener? = null

    private var unReadMsgTipListener: UnReadMsgTipListener? = null


    private val accountService: IAccountService? = ServiceImpl.getInstance().getService(IAccountService::class.java.name) as? IAccountService

    private val accountObserver = object : IAccountService.IAccountObserver {
        override fun onLogin() {
            connect()
        }

        override fun onLogout() {
            disconnect()
        }

        override fun onUpdate() {

        }

    }


    fun init() {
        initSocket()
        connect()
        MessageManager.getInstance().attachMessage(IAccountService.IAccountObserver.EVENT_ID, accountObserver)
    }

    private fun initSocket() {
        try {
            mSocket = IO.socket("http://43.138.100.114:8001/chat_room")

        } catch (e: URISyntaxException) {
            print(e.message)
        }

        mSocket?.on("send_broadcast") { args ->
            if (args.isEmpty()) return@on
            val msg = (args[0] as? String) ?: return@on
            val recListener = receiveMessageListener
            val msgEntity = parseSingleMsg(msg) ?: return@on
            if (recListener != null) {
                lastReadMsgId = msgEntity.msgId
                unReadMsgCount = 0
                //可以通过消息回调上报给服务信息读取状态，保存到服务，简单先存本地也不区分用户了
                ConfMgr.setStringValue("", "last_read_msg", lastReadMsgId, false)
                recListener.onMsgReceive(msgEntity)
            } else {
                if (lastReadMsgId == null || lastReadMsgId == "none") {
                    lastReadMsgId = msgEntity.msgId + "_include"
                    ConfMgr.setStringValue("", "last_read_msg", lastReadMsgId, false)
                }
                unReadMsgTipListener?.unReadMsg(++unReadMsgCount)
            }
            Timber.tag("immmm").e("my_response $args")
        }
    }

    private fun connect() {
        if (connected) return
        if (accountService?.isLogin() != true) return
        Timber.tag("immmm").e("connect")
        connected = true
        mSocket?.connect()
    }

    private fun disconnect() {
        mSocket?.disconnect()
    }

    fun sendMsg(msg: String) {
        if (accountService?.isLogin() != true) return
        val json = JSONObject()
        json.putOpt("user_id", accountService.getAccountInfo().getUserId())
        json.putOpt("user_name", accountService.getAccountInfo().getNickName())
        json.putOpt("msg", msg)

        mSocket?.emit("new_message", json.toString())
    }

    fun isMineSend(messageEntity: MessageEntity): Boolean {
        return messageEntity.userId == accountService?.getAccountInfo()?.getUserId()
    }

    fun setOnReceiveMessageListener(onReceiveMessageListener: OnReceiveMessageListener) {
        this.receiveMessageListener = onReceiveMessageListener
        unReadMsgCount = 0
        lastReadMsgId = "none"
        ConfMgr.setStringValue("", "last_read_msg", lastReadMsgId, false)
        unReadMsgTipListener?.unReadMsg(0)
    }

    fun removeOnReceiveMessageListener() {
        this.receiveMessageListener = null
    }

    fun setUnReadMsgTipListener(unReadMsgTipListener: UnReadMsgTipListener) {
        this.unReadMsgTipListener = unReadMsgTipListener
        getUnReadMsgCountFromServer()
    }

    private fun getUnReadMsgCountFromServer() {
        if (lastReadMsgId == null) {
            lastReadMsgId = ConfMgr.getStringValue("", "last_read_msg", null) ?: return
        }
        if (lastReadMsgId == "none") return

        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet("http://43.138.100.114:8001/unread?last_read_id=$lastReadMsgId")) {
            if (!it.isSuccessful) return@asyncGet
            it.dataToString().toInt().apply {
                unReadMsgCount = this
                unReadMsgTipListener?.unReadMsg(this)
            }
        }

    }


    interface OnReceiveMessageListener {
        fun onMsgReceive(msg: MessageEntity)
    }

    interface UnReadMsgTipListener {
        fun unReadMsg(count: Int)
    }

}