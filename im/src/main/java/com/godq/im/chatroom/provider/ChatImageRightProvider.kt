package com.godq.im.chatroom.provider

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.facebook.drawee.view.SimpleDraweeView
import com.godq.im.R
import com.godq.im.chatroom.MessageEntity
import com.godq.im.databinding.ImChatImgRightItemBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder

class ChatImageRightProvider : BaseItemProvider<MultiItemEntity, DataBindBaseViewHolder>() {

    override fun viewType(): Int {
        return MessageEntity.IMG_RIGHT
    }

    override fun layout(): Int {
        return R.layout.im_chat_img_right_item
    }

    override fun convert(helper: DataBindBaseViewHolder?, data: MultiItemEntity?, position: Int) {
        if (helper?.viewDataBinding !is ImChatImgRightItemBinding) {
            return
        }
        if (data !is MessageEntity) {
            return
        }

        val realW = data.picWidth ?: return
        val realH = data.picHeight ?: return
        if (realW == 0 || realH == 0) return
        val imgView = helper.getView<SimpleDraweeView>(R.id.self_iv)
        val realHeight = realH.toFloat() / realW * imgView.layoutParams.width
        imgView.layoutParams.height = realHeight.toInt()
        imgView.requestLayout()

        helper.addOnClickListener(R.id.other_iv)
        helper.addOnClickListener(R.id.self_iv)
        val binding = helper.viewDataBinding as ImChatImgRightItemBinding
        binding.msg = data
        binding.executePendingBindings()

    }

}