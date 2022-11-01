package com.godq.im.chatroom

import com.chad.library.adapter.base.entity.MultiItemEntity

class MessageEntity : MultiItemEntity{

    companion object {
        const val TEXT = 100
        const val IMG = 101
    }

    var msgId: String? = null
    var userId: String? = null
    var msg: String? = null
    var nickName: String? = null
    var msgType: Int? = null
    var userAvatar: String? = null


    override fun getItemType(): Int {
        return if (msgType == 0) TEXT else IMG
    }

    override fun toString(): String {
        return "MessageEntity(msgId=$msgId, userId=$userId, msg=$msg, nickName=$nickName, msgType=$msgType, userAvatar=$userAvatar)"
    }


}