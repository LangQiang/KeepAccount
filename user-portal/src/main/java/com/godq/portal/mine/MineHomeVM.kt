package com.godq.portal.mine

import androidx.lifecycle.*
import com.godq.accountsa.IAccountInfo
import com.godq.accountsa.IAccountService
import com.godq.deeplink.DeepLinkUtils
import com.godq.portal.UserPortalLinkHelper
import com.lazylite.mod.messagemgr.MessageManager
import timber.log.Timber

class MineHomeVM : LifecycleEventObserver {
    val mineHomeUIState = MineHomeUIState()

    private val accountObserver = object : IAccountService.IAccountObserver {
        override fun onLogin() {
            Timber.tag("test").e("onLogin")
            updateLoginUI()
        }

        override fun onLogout() {
            Timber.tag("test").e("onLogout")
            updateLoginUI()
        }

    }

    fun updateLoginUI() {
        if (UserPortalLinkHelper.isLogin()) {
            setUIStateByAccountInfo(UserPortalLinkHelper.getAccountInfo())
        } else {
            setUIStateByAccountInfo(null)
        }
    }

    private fun setUIStateByAccountInfo(accountInfo: IAccountInfo?) {
        if (accountInfo == null) {
            mineHomeUIState.mineHomeTitleID = MineHomeUIState.DEFAULT_TITLE_ID
            mineHomeUIState.mineHomeTitleName = MineHomeUIState.DEFAULT_TITLE_NAME
            mineHomeUIState.mineHomeTitleImg = ""
        } else {
            mineHomeUIState.mineHomeTitleID = "id:${accountInfo.getUserId()}"
            mineHomeUIState.mineHomeTitleName = accountInfo.getNickName()
            mineHomeUIState.mineHomeTitleImg = ""
        }
    }

    fun openLoginPage() {
        if (UserPortalLinkHelper.isLogin()) return
        DeepLinkUtils.load("test://open/account?type=login").execute()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Timber.tag("test").e(event.name)
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                MessageManager.getInstance().attachMessage(IAccountService.IAccountObserver.EVENT_ID, accountObserver)
            }
            Lifecycle.Event.ON_DESTROY -> {
                MessageManager.getInstance().detachMessage(IAccountService.IAccountObserver.EVENT_ID, accountObserver)
            }
            else -> {
                //ignore
            }
        }
    }

}