package com.godq.keepaccounts

import android.app.Application
import android.content.Context
import android.os.Process
import com.godq.compose.UICompose
import com.godq.compose.UIComposeConfig
import com.lazylite.bridge.init.ComponentInit
import com.lazylite.mod.global.BaseConfig
import com.lazylite.mod.utils.KwDebug
import com.lazylite.mod.utils.KwLifecycleCallback
import kotlin.system.exitProcess

class KAApp : Application() {

    companion object {
        @JvmField var context:KAApp? = null

        private var isAgreeProtocolWhenStart:Boolean = false

        @JvmStatic fun getInstance() : Context? {
            return context
        }

        @JvmStatic fun initAfterAgreeProtocol() {
//            if (!isAgreeProtocolWhenStart) {
//                ComponentInit.initAfterAgreeProtocol(getInstance())
//            }
        }
        @JvmStatic fun exitApp() {
            KwDebug.mustMainThread()
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        val baseConfig = BaseConfig()
        baseConfig.allowProxy = true
        baseConfig.deepLinkScheme = "test"
        ComponentInit.initOnAppCreate(this.applicationContext, baseConfig)




//        ComponentInit.initOnAppCreate(this)
//        isAgreeProtocolWhenStart = ProtocolUtil.isProtocolDialogAgreed
//        if (isAgreeProtocolWhenStart) {
//            ComponentInit.initAfterAgreeProtocol(this)
//        }

        registerActivityLifecycleCallbacks(KwLifecycleCallback.getInstance())
    }
}