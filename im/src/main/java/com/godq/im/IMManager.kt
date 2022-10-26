package com.godq.im

import com.godq.accountsa.IAccountService
import com.godq.im.chatroom.MessageEntity
import com.lazylite.bridge.router.ServiceImpl
import com.lazylite.mod.messagemgr.MessageManager
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import timber.log.Timber
import java.net.URISyntaxException

object IMManager {

    private var connected = false

    private var mSocket: Socket? = null

    private val receiveMessageListeners = ArrayList<OnReceiveMessageListener>()


    private val accountService: IAccountService? = ServiceImpl.getInstance().getService(IAccountService::class.java.name) as? IAccountService

    private val accountObserver = object : IAccountService.IAccountObserver {
        override fun onLogin() {
            connect()
        }

        override fun onLogout() {
            disconnect()
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
            for (receiveMessageListener in receiveMessageListeners) {
                receiveMessageListener.onMsgReceive(msg)
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
        json.putOpt("msg", msg)

        mSocket?.emit("new_message", json.toString())
    }

    fun isMineSend(messageEntity: MessageEntity): Boolean {
        return messageEntity.userId == accountService?.getAccountInfo()?.getUserId()
    }

    fun addOnReceiveMessageListener(onReceiveMessageListener: OnReceiveMessageListener) {
        receiveMessageListeners.add(onReceiveMessageListener)
    }

    fun removeOnReceiveMessageListener(onReceiveMessageListener: OnReceiveMessageListener) {
        receiveMessageListeners.remove(onReceiveMessageListener)
    }

    interface OnReceiveMessageListener {
        fun onMsgReceive(msg: String)
    }

}