package com.godq.im

import android.net.Uri
import com.godq.deeplink.route.AbsRouter
import com.godq.im.chatroom.ChatRoomFragment
import com.lazylite.annotationlib.DeepLink
import com.lazylite.mod.fragmentmgr.FragmentOperation

@DeepLink(path = "/chat/room")
class ChatRoomRouter: AbsRouter() {
    override fun parse(p0: Uri?) {

    }

    override fun route(): Boolean {
        FragmentOperation.getInstance().showFullFragment(ChatRoomFragment())
        return true
    }
}