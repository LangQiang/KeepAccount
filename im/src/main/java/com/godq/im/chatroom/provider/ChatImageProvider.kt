package com.godq.im.chatroom.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.godq.im.R
import com.godq.im.chatroom.MessageEntity
import com.godq.im.databinding.ImChatImgItemBinding
import com.godq.im.databinding.ImChatTextItemBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class ChatImageProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return MessageEntity.IMG
    }

    override fun layout(): Int {
        return R.layout.im_chat_img_item
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is ImChatImgItemBinding) {
            return
        }
        if (data !is MessageEntity) {
            return
        }
        val binding = helper.viewDataBinding as ImChatImgItemBinding
        binding.msg = data
        binding.executePendingBindings()
    }

}