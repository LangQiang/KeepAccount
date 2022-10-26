package com.godq.im

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class Test {

    private var mSocket: Socket? = null

    fun test() {
        try {
            mSocket = IO.socket("http://chat.socket.io")
        } catch (e: URISyntaxException) {
        }
        mSocket?.connect()

        val message = "test"

        mSocket?.emit("new message", message)
    }

}