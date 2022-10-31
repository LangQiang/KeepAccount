package com.godq.im.chatroom

import com.chad.library.adapter.base.entity.MultiItemEntity

class MessageEntity : MultiItemEntity{

    companion object {
        const val TEXT = 100
    }

    var msgId: String? = null
    var userId: String? = null
    var msg: String? = null
    var userName: String? = null
    var msgType: Int? = null
    override fun getItemType(): Int {
        return TEXT
    }

    override fun toString(): String {
        return "MessageEntity(msgId=$msgId, userId=$userId, msg=$msg, msgType=$msgType)"
    }


}