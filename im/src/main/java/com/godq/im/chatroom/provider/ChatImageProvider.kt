package com.godq.im.chatroom.provider

import android.graphics.drawable.Animatable
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.godq.im.R
import com.godq.im.chatroom.ConstantRes
import com.godq.im.chatroom.MessageEntity
import com.godq.im.databinding.ImChatImgItemBinding
import com.lazylite.mod.imageloader.fresco.ImageLoaderWapper
import com.lazylite.mod.imageloader.fresco.listener.IDisplayImageListener
import com.lazylite.mod.utils.DataBindBaseViewHolder
import com.lazylite.mod.utils.DeviceInfo

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

        val imgView = helper.getView<SimpleDraweeView>(R.id.self_iv)
        val imgView2 = helper.getView<SimpleDraweeView>(R.id.other_iv)
        val realHeight = DeviceInfo.HEIGHT.toFloat() / DeviceInfo.WIDTH * imgView.layoutParams.width
        imgView.layoutParams.height = realHeight.toInt()
        imgView2.layoutParams.height = realHeight.toInt()
        imgView.requestLayout()
        imgView2.requestLayout()

        helper.addOnClickListener(R.id.other_iv)
        helper.addOnClickListener(R.id.self_iv)
        val binding = helper.viewDataBinding as ImChatImgItemBinding
        binding.msg = data
        binding.executePendingBindings()


    }

}