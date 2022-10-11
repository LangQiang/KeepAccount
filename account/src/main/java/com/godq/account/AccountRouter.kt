package com.godq.account

import android.net.Uri
import com.godq.account.login.LoginFragment
import com.godq.account.register.RegistrationFragment
import com.godq.deeplink.route.AbsRouter
import com.lazylite.annotationlib.DeepLink
import com.lazylite.mod.fragmentmgr.FragmentOperation

@DeepLink(path = "/account")
class AccountRouter: AbsRouter() {

    private var type: String? = null

    override fun parse(uri: Uri?) {
        type = uri?.getQueryParameter("type")
    }

    override fun route(): Boolean {
        when (type) {
            "register" -> {
                FragmentOperation.getInstance().showFullFragment(RegistrationFragment())
            }
            "login" -> {
                FragmentOperation.getInstance().showFullFragment(LoginFragment())
            }
            else -> {
                FragmentOperation.getInstance().showFullFragment(LoginFragment())
            }
        }
        return true
    }
}