package com.godq.cms

import android.net.Uri
import com.godq.deeplink.route.AbsRouter
import com.lazylite.annotationlib.DeepLink
import com.lazylite.mod.fragmentmgr.FragmentOperation

@DeepLink(path = "/cms/index")
class BillMgrRouter: AbsRouter() {
    override fun parse(p0: Uri?) {

    }

    override fun route(): Boolean {
        FragmentOperation.getInstance().showFullFragment(BillMgrHomeFragment())
        return true
    }
}