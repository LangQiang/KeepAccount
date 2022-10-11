package com.godq.account.register.repository

class RegisterRemoteDataSource(private val api: IRegisterApi) {

    fun register(registerInfo: RegisterInfo): Boolean = api.register(registerInfo)

    interface IRegisterApi {
        fun register(registerInfo: RegisterInfo): Boolean
    }
}