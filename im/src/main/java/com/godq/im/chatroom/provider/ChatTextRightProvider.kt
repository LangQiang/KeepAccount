package com.godq.im.chatroom.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.godq.im.R
import com.godq.im.chatroom.MessageEntity
import com.godq.im.databinding.ImChatTextRightItemBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class ChatTextRightProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return MessageEntity.TEXT_RIGHT
    }

    override fun layout(): Int {
        return R.layout.im_chat_text_right_item
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is ImChatTextRightItemBinding) {
            return
        }
        if (data !is MessageEntity) {
            return
        }
        val binding = helper.viewDataBinding as ImChatTextRightItemBinding
        binding.msg = data
        binding.executePendingBindings()
    }

}