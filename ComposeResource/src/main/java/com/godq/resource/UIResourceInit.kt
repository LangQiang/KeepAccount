package com.godq.resource

import android.content.Context
import android.util.Pair
import com.godq.compose.UICompose
import com.godq.compose.UIComposeConfig
import com.lazylite.annotationlib.AutoInit
import com.lazylite.bridge.init.Init

@AutoInit
class UIResourceInit: Init() {

    override fun init(p0: Context?) {
        //UiComposeInit
        p0?: return
        with(UIComposeConfig()) {
            this.fontAssetsPath = "fonts/iconfont.ttf"
            UICompose.init(p0, this)
        }
    }

    override fun initAfterAgreeProtocol(p0: Context?) {
    }

    override fun getServicePair(): Pair<String, Any>? {
        return null
    }

}