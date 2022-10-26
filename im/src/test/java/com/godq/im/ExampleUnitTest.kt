package com.godq.im

import android.os.SystemClock
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import java.net.URISyntaxException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        test()
    }

    private var mSocket: Socket? = null

    fun test() {

        Thread {
            try {
                mSocket = IO.socket("http://43.138.100.114:8001/chat_room")

            } catch (e: URISyntaxException) {
                print(e.message)
            }
            print("connect!!")


            mSocket?.on("send_broadcast") { args ->
                print("my_response")
                mSocket?.emit("xxxx", "xxxx${mSocket?.id()}")

                print(args)
            }

            mSocket?.connect()

            val json = JSONObject()
            json.putOpt("user_id", "test_user_id")
            json.putOpt("msg", "test_msg")

            mSocket?.emit("new_message", json.toString())
            mSocket?.emit("new_message", json.toString())
            mSocket?.emit("new_message", json.toString())
        }.start()


        while (true) {
            Thread.sleep(100000)
        }

    }
}