package com.godq.im.chatroom.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.godq.im.R
import com.godq.im.chatroom.MessageEntity
import com.godq.im.databinding.ImChatTextItemBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class ChatTextProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return MessageEntity.TEXT
    }

    override fun layout(): Int {
        return R.layout.im_chat_text_item
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is ImChatTextItemBinding) {
            return
        }
        if (data !is MessageEntity) {
            return
        }
        val binding = helper.viewDataBinding as ImChatTextItemBinding
        binding.msg = data
        binding.executePendingBindings()
    }

}