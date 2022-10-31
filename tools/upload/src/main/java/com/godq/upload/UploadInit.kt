package com.godq.upload

import android.content.Context
import android.util.Pair
import com.godq.ulda.IUploadService
import com.lazylite.annotationlib.AutoInit
import com.lazylite.bridge.init.Init

@AutoInit
class UploadInit: Init() {
    override fun init(context: Context?) {

    }

    override fun initAfterAgreeProtocol(context: Context?) {

    }

    override fun getServicePair(): Pair<String, Any> {
        return Pair(IUploadService::class.java.name, UploadServiceImpl())
    }
}