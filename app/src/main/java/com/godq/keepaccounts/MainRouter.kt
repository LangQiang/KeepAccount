package com.godq.keepaccounts

import android.net.Uri
import com.godq.deeplink.route.AbsRouter
import com.lazylite.annotationlib.DeepLink
import com.lazylite.mod.App

@DeepLink(path = "/main/switch")
class MainRouter: AbsRouter() {

    override fun parse(p0: Uri?) {

    }

    override fun route(): Boolean {
        (App.getMainActivity() as? MainActivity)?.switchMode(GuideController.OPT_SWITCH)
        return true
    }
}