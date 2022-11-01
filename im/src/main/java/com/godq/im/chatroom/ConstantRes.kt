package com.godq.im.chatroom

import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig

object ConstantRes {
    val avatarConfig: ImageLoadConfig = ImageLoadConfig.Builder()
        .roundedCorner(8f)
        .create()
}