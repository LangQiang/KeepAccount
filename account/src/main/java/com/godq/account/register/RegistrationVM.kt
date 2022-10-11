package com.godq.account.register

import com.godq.account.RepositorySFactory
import com.godq.account.register.repository.RegisterInfo
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.utils.toast.KwToast
import kotlinx.coroutines.*

class RegistrationVM {

    val registerUIState = RegisterUIState()

    private val vmScope = CoroutineScope(Job() + Dispatchers.Main)

    fun register() {
        vmScope.launch {
            registerUIState.pageState = RegisterUIState.PAGE_TYPE_LOADING
            delay(4000)
            if (RepositorySFactory.getRegisterRepository().register(transferUIStateToRepoInput(registerUIState))) {
                close()
            } else {
                KwToast.show("注册失败")
            }
            registerUIState.pageState = RegisterUIState.PAGE_TYPE_DEFAULT
        }
    }

    fun close() {
        if (FragmentOperation.getInstance().topFragment is RegistrationFragment) {
            FragmentOperation.getInstance().close()
        }
    }

    private fun transferUIStateToRepoInput(registerUIState: RegisterUIState): RegisterInfo {
        return RegisterInfo(registerUIState.accountName, registerUIState.password, registerUIState.nickName)
    }
}