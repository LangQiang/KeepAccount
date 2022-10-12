package com.godq.portal

import android.content.Context
import android.util.Pair
import com.godq.upa.IUserPortalService
import com.lazylite.annotationlib.AutoInit
import com.lazylite.bridge.init.Init

@AutoInit
class UserPortalInit: Init() {
    override fun init(p0: Context?) {
    }

    override fun initAfterAgreeProtocol(p0: Context?) {
    }

    override fun getServicePair(): Pair<String, Any> {
        return Pair(IUserPortalService::class.java.name, UserPortalService())
    }
}