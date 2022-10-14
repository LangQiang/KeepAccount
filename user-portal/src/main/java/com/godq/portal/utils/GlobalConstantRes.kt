package com.godq.portal.utils

import com.godq.portal.R
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig

object GlobalConstantRes {
//    companion object {
//        @JvmStatic
//        val commonImageConfig: ImageLoadConfig = ImageLoadConfig.Builder()
//            .roundedCorner(16f)
//            .create()
//    }
    val commonImageConfig: ImageLoadConfig = ImageLoadConfig.Builder()
        .roundedCorner(16f)
        .create()
    val mineHeadImageConfig: ImageLoadConfig = ImageLoadConfig.Builder()
        .circle()
        .setFailureDrawable(R.drawable.mine_default_head_avatar)
        .create()
}