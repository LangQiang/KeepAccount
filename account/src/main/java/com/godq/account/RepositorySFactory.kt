package com.godq.account

import com.godq.account.login.repository.LoginLocalDataSource
import com.godq.account.login.repository.LoginRemoteDataSource
import com.godq.account.login.repository.LoginRepository
import com.godq.account.login.repository.inject.LoginLocalApiImpl
import com.godq.account.login.repository.inject.LoginRemoteApiImpl
import com.godq.account.register.inject.RegisterApiImpl
import com.godq.account.register.repository.RegisterRemoteDataSource
import com.godq.account.register.repository.RegistrationRepository

class RepositorySFactory {
    companion object {
        private val mRegistrationRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RegistrationRepository(RegisterRemoteDataSource(RegisterApiImpl()))
        }
        @JvmStatic
        fun getRegisterRepository(): RegistrationRepository {
            return mRegistrationRepository
        }

        private val mLoginRepository by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LoginRepository(LoginRemoteDataSource(LoginRemoteApiImpl()), LoginLocalDataSource(LoginLocalApiImpl()))
        }
        @JvmStatic
        fun getLoginRepository(): LoginRepository {
            return mLoginRepository
        }
    }
}