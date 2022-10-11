package com.godq.account.register.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegistrationRepository (
    private val registerRemoteDataSource: RegisterRemoteDataSource
    ) {
    suspend fun register(registerInfo: RegisterInfo): Boolean = withContext(Dispatchers.IO) {
        registerRemoteDataSource.register(registerInfo)
    }
}