package com.godq.keepaccounts.utils

import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig

class GlobalConstantRes {
    companion object {
        @JvmStatic
        val commonImageConfig: ImageLoadConfig = ImageLoadConfig.Builder()
            .roundedCorner(16f)
            .create()
    }
}