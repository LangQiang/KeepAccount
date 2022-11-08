package com.godq.im.chatroom

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.godq.im.PublicChatRoomManager

class MessageEntity(
    var msgId: String?,
    var userId: String?,
    var msg: String?,
    var nickName: String?,
    var msgType: Int?,
    var userAvatar: String?,
    var picWidth: Int?,
    var picHeight: Int?
) : MultiItemEntity{


    companion object {
        const val LEFT_MARK =  0x1000_0000
        const val RIGHT_MARK = 0x0100_0000
        const val TEXT_MARK  = 0x0000_0001
        const val IMG_MARK  =  0x0000_0010

        const val TEXT_LEFT =       LEFT_MARK or TEXT_MARK
        const val TEXT_RIGHT =      RIGHT_MARK or TEXT_MARK
        const val IMG_LEFT =        LEFT_MARK or IMG_MARK
        const val IMG_RIGHT =       RIGHT_MARK or IMG_MARK
    }

    private val itemType: Int

    init {
        val origin = if (PublicChatRoomManager.isMineSend(this)) RIGHT_MARK else LEFT_MARK
        val type = if (msgType == 0) TEXT_MARK else IMG_MARK
        itemType = origin or type
    }



    override fun getItemType(): Int {
        return itemType
    }

    override fun toString(): String {
        return "MessageEntity(msgId=$msgId, userId=$userId, msg=$msg, nickName=$nickName, msgType=$msgType, userAvatar=$userAvatar, picWidth=$picWidth, picHeight=$picHeight)"
    }


}