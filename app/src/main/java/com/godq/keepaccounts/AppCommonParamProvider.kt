package com.godq.keepaccounts

import com.lazylite.mod.http.mgr.ICommonParamProvider
import com.lazylite.mod.utils.LRSign
import java.util.concurrent.ConcurrentHashMap

object AppCommonParamProvider: ICommonParamProvider {

    private val headers = ConcurrentHashMap<String, String>()

    private val queryParams = ConcurrentHashMap<String, String>()

    override fun getCommonHeads(): ConcurrentHashMap<String, String> {
        val timestamp = System.currentTimeMillis().toString()
        val nonce = LRSign.getRandomString(6)
        headers["timestamp"] = timestamp
        headers["nonce"] = nonce
        headers["sign"] = MainLinkHelper.getMD5("857", "${MainLinkHelper.getToken()}#$timestamp#$nonce")
        return headers
    }

    override fun getCommonQueryParams(): ConcurrentHashMap<String, String> {
        return queryParams
    }

    override fun providerName(): String {
        return "App"
    }

    fun updateHeader(key: String, value: String) {
        headers[key] = value
    }

    fun updateQueryParam(key: String, value: String) {
        queryParams[key] = value
    }
}