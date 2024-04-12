package com.godq.cms.update

import android.net.Uri
import com.godq.deeplink.route.AbsRouter
import com.lazylite.annotationlib.DeepLink
import com.lazylite.mod.fragmentmgr.FragmentOperation

@DeepLink(path = "/cms/upload")
class BillUpdateRouter: AbsRouter() {
    override fun parse(p0: Uri?) {

    }

    override fun route(): Boolean {
        FragmentOperation.getInstance().showFullFragment(BillUpdateFragment())
        return true
    }
}