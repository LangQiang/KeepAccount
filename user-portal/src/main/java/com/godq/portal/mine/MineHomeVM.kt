package com.godq.portal.mine

import androidx.lifecycle.*
import com.godq.accountsa.IAccountInfo
import com.godq.accountsa.IAccountService
import com.godq.deeplink.DeepLinkUtils
import com.godq.portal.UserPortalLinkHelper
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import org.json.JSONObject
import timber.log.Timber

class MineHomeVM : LifecycleEventObserver {
    val mineHomeUIState = MineHomeUIState()
    val mineHomeDataUIState = MineHomeDataUIState()

    private val accountObserver = object : IAccountService.IAccountObserver {
        override fun onLogin() {
            Timber.tag("account").e("onLogin")
            requestAndUpdateUI()
        }

        override fun onLogout() {
            Timber.tag("account").e("onLogout")
            requestAndUpdateUI()
        }

    }

    fun requestAndUpdateUI() {
        if (UserPortalLinkHelper.isLogin()) {
            setUIStateByAccountInfo(UserPortalLinkHelper.getAccountInfo())
            requestTurnoverData()
        } else {
            setUIStateByAccountInfo(null)
        }
    }

    private fun requestTurnoverData() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet("http://150.158.55.208/bill/statistics")) {
            try {
                JSONObject(it.dataToString()).optJSONObject("data")?.apply {
                    mineHomeDataUIState.mineHomeTotalTurnover = optDouble("totalTurnover")
                    mineHomeDataUIState.mineHomeLastDayTurnover = optDouble("yesterdayTurnover")
                    mineHomeDataUIState.mineHomeCurrentWeekTurnover = optDouble("currentWeekTotalTurnover")
                    mineHomeDataUIState.mineHomeLastWeekTurnover = optDouble("lastWeekTotalTurnover")
                    mineHomeDataUIState.mineHomeCurrentMonthTurnover = optDouble("currentMonthTotalTurnover")
                    mineHomeDataUIState.mineHomeLastMonthTurnover = optDouble("lastMonthTotalTurnover")
                }
            } catch (e: Exception) {

            }
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
//            Lifecycle.Event.ON_RESUME -> {
//                updateLoginUI()
//            }
            else -> {
                //ignore
            }
        }
    }

    fun gotoSettingPage() {
        DeepLinkUtils.load("test://open/cms/update").execute()
    }

}