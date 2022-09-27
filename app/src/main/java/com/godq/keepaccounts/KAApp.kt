package com.godq.keepaccounts

import android.app.Application
import android.content.Context
import android.os.Process
import com.lazylite.mod.global.CommonInit
import com.lazylite.mod.utils.KwDebug
import com.lazylite.mod.utils.KwLifecycleCallback

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
            System.exit(0)
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        CommonInit.initOnAppCreate(this.applicationContext)


//        ComponentInit.initOnAppCreate(this)
//        isAgreeProtocolWhenStart = ProtocolUtil.isProtocolDialogAgreed
//        if (isAgreeProtocolWhenStart) {
//            ComponentInit.initAfterAgreeProtocol(this)
//        }

        registerActivityLifecycleCallbacks(KwLifecycleCallback.getInstance())
    }
}