package com.godq.im.chatroom

import com.chad.library.adapter.base.entity.MultiItemEntity

class MessageEntity : MultiItemEntity{

    companion object {
        const val TEXT = 100
    }

    var userId: String? = null
    var msg: String? = null
    var msgType: Int? = null
    override fun getItemType(): Int {
        return TEXT
    }
}