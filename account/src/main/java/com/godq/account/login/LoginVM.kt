package com.godq.account.login

import com.godq.account.RepositorySFactory
import com.godq.account.login.repository.LoginInfo
import com.godq.account.register.repository.RegisterInfo
import com.godq.deeplink.DeepLinkConstants
import com.godq.deeplink.DeepLinkUtils
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.utils.toast.KwToast
import kotlinx.coroutines.*

class LoginVM {

    val loginUIState = LoginUIState()

    private val vmScope = CoroutineScope(Job() + Dispatchers.Main)

    fun login() {
        vmScope.launch {
            loginUIState.pageState = LoginUIState.PAGE_TYPE_LOADING
            val loginInfo = RepositorySFactory.getLoginRepository().login(transferUIStateToRepoInput(loginUIState))
            if (loginInfo.isLogin()) {
                close()
            } else {
                KwToast.show("登录失败")
            }
            loginUIState.pageState = LoginUIState.PAGE_TYPE_DEFAULT
        }
    }

    fun goToRegisterPage() {
        DeepLinkUtils.load("test://open/account?type=register").execute()
    }

    fun close() {
        if (FragmentOperation.getInstance().topFragment is LoginFragment) {
            FragmentOperation.getInstance().close()
        }
    }

    private fun transferUIStateToRepoInput(loginUIState: LoginUIState): LoginInfo {
        return LoginInfo(loginUIState.accountName, loginUIState.password)
    }
}