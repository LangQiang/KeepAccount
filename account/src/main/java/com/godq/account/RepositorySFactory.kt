package com.godq.account

import com.godq.account.register.inject.RegisterApiImpl
import com.godq.account.register.repository.RegisterRemoteDataSource
import com.godq.account.register.repository.RegistrationRepository

class RepositorySFactory {
    companion object {
        private val registratitonRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RegistrationRepository(RegisterRemoteDataSource(RegisterApiImpl()))
        }
        @JvmStatic
        fun getRegisterRepository(): RegistrationRepository {
            return registratitonRepository
        }
    }
}