package com.godq.im.chatroom

import com.facebook.drawee.drawable.ScalingUtils
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig

object ConstantRes {
    val avatarConfig: ImageLoadConfig = ImageLoadConfig.Builder()
        .roundedCorner(8f)
        .create()

    val chatImgConfig: ImageLoadConfig = ImageLoadConfig.Builder()
//        .roundedCorner(8f)
        .setScaleType(ScalingUtils.ScaleType.FIT_CENTER)
        .create()
}