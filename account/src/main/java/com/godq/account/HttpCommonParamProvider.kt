package com.godq.account

import com.lazylite.mod.http.mgr.ICommonParamProvider
import java.util.concurrent.ConcurrentHashMap

object HttpCommonParamProvider: ICommonParamProvider {

    private val headers = ConcurrentHashMap<String, String>()
    private val queryParams = ConcurrentHashMap<String, String>()

    override fun getCommonHeads(): ConcurrentHashMap<String, String> {
        return headers
    }

    override fun getCommonQueryParams(): ConcurrentHashMap<String, String> {
        return queryParams
    }

    override fun providerName(): String {
        return "AccountService"
    }

    fun updateHeader(key: String, value: String) {
        headers[key] = value
    }

    fun updateQueryParam(key: String, value: String) {
        queryParams[key] = value
    }
}