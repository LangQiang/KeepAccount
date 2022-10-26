package com.godq.im

import android.content.Context
import android.util.Pair
import com.lazylite.annotationlib.AutoInit
import com.lazylite.bridge.init.Init

@AutoInit
class IMInit: Init() {
    override fun init(context: Context?) {
        IMManager.init()
    }

    override fun initAfterAgreeProtocol(context: Context?) {
    }

    override fun getServicePair(): Pair<String, Any>? {
        return null
    }
}