package com.godq.cms

import android.content.Context
import android.util.Pair
import com.godq.msa.IManagerService
import com.lazylite.annotationlib.AutoInit
import com.lazylite.bridge.init.Init


/**
 * @author  GodQ
 * @date  2023/4/25 3:37 下午
 */
@AutoInit
class MgrInit: Init() {
    override fun init(context: Context?) {
    }

    override fun initAfterAgreeProtocol(context: Context?) {
    }

    override fun getServicePair(): Pair<String, Any> {
        return Pair(IManagerService::class.java.name, ManagerServiceImpl())
    }
}