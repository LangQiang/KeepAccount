package com.godq.im.chatroom

import com.chad.library.adapter.base.MultipleItemRvAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.godq.im.chatroom.provider.ChatImageRightProvider
import com.godq.im.chatroom.provider.ChatImageLeftProvider
import com.godq.im.chatroom.provider.ChatTextLeftProvider
import com.godq.im.chatroom.provider.ChatTextRightProvider
import com.lazylite.mod.utils.DataBindBaseViewHolder

class ChatRoomAdapter(data: List<MultiItemEntity>?): MultipleItemRvAdapter<MultiItemEntity, DataBindBaseViewHolder>(
    data) {

    init {
        finishInitialize()
    }

    override fun getViewType(p0: MultiItemEntity?): Int {
        return p0?.itemType ?: -1
    }

    override fun registerItemProvider() {
        mProviderDelegate.registerProvider(ChatTextLeftProvider())
        mProviderDelegate.registerProvider(ChatTextRightProvider())
        mProviderDelegate.registerProvider(ChatImageLeftProvider())
        mProviderDelegate.registerProvider(ChatImageRightProvider())
    }
}