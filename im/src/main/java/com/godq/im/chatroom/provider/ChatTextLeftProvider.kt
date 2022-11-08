package com.godq.im.chatroom.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.godq.im.R
import com.godq.im.chatroom.MessageEntity
import com.godq.im.databinding.ImChatTextLeftItemBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class ChatTextLeftProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return MessageEntity.TEXT_LEFT
    }

    override fun layout(): Int {
        return R.layout.im_chat_text_left_item
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is ImChatTextLeftItemBinding) {
            return
        }
        if (data !is MessageEntity) {
            return
        }
        val binding = helper.viewDataBinding as ImChatTextLeftItemBinding
        binding.msg = data
        binding.executePendingBindings()
    }

}