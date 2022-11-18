package com.godq.portal.mine

import androidx.lifecycle.*
import com.godq.accountsa.IAccountInfo
import com.godq.accountsa.IAccountService
import com.godq.deeplink.DeepLinkUtils
import com.godq.msa.IManagerSystemObserver
import com.godq.portal.UserPortalLinkHelper
import com.godq.portal.constants.HOST
import com.godq.portal.utils.jumpToSettingFragment
import com.godq.threadpool.ThreadPool
import com.godq.ulda.IUploadService
import com.lazylite.mod.App
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.utils.KwFileUtils
import com.lazylite.mod.utils.toast.KwToast
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.util.*

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

        override fun onUpdate() {
            Timber.tag("account").e("onUpdate")
            requestAndUpdateUI()
        }

    }

    private val managerSystemObserver = object : IManagerSystemObserver {
        override fun onBillUpdate() {
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
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet("$HOST/bill/statistics")) {
            try {
                val data = it.dataToString()
                Timber.tag("statistic").e(data)
                JSONObject(data).optJSONObject("data")?.apply {
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
            mineHomeUIState.mineHomeTitleImg = accountInfo.getAvatarUrl()
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
                fakeCache()
                MessageManager.getInstance().attachMessage(IAccountService.IAccountObserver.EVENT_ID, accountObserver)
                MessageManager.getInstance().attachMessage(IManagerSystemObserver.EVENT_ID, managerSystemObserver)
            }
            Lifecycle.Event.ON_DESTROY -> {
                MessageManager.getInstance().detachMessage(IAccountService.IAccountObserver.EVENT_ID, accountObserver)
                MessageManager.getInstance().detachMessage(IManagerSystemObserver.EVENT_ID, managerSystemObserver)
            }
            Lifecycle.Event.ON_RESUME -> {
                if (UserPortalLinkHelper.isLogin()) {
                    requestTurnoverData()
                }
            }
            else -> {
                //ignore
            }
        }
    }

    fun gotoSettingPage() {
        if (!UserPortalLinkHelper.isLogin()) {
            KwToast.show("去登录")
            return
        }
        jumpToSettingFragment()
    }

    fun onAvatarClick() {
        if (mineHomeUIState.mineHomeTitleID == MineHomeUIState.DEFAULT_TITLE_ID
//            || mineHomeUIState.mineHomeTitleImg.isNotEmpty()
        ) return
        val activity = App.getMainActivity()?: return
        UserPortalLinkHelper.chooseImage(activity, object : IUploadService.OnChooseImageCallback {
            override fun onChoose(fileUri: String?) {
                Timber.tag("mine").e("filePath: $fileUri")
                uploadFileByPathToCos(fileUri)
            }

        })
    }

    private fun uploadFileByPathToCos(filePath: String?) {
        if (filePath.isNullOrEmpty()) {
            KwToast.show("上传失败，无法获取所选文件路径")
            return
        }
//        val path = try {
//            Uri.parse(filePath).path
//        } catch (e: Exception) {
//            null
//        } ?: return
        UserPortalLinkHelper.upload(filePath, object : IUploadService.OnUploadCallback {
            override fun onUpload(accessUrl: String?) {
                if (accessUrl.isNullOrEmpty()) {
                    KwToast.show("无法上传文件到服务器")
                    return
                }
                UserPortalLinkHelper.updateAvatar(accessUrl)
            }

        })

    }

    private fun fakeCache() {
        ThreadPool.exec {
            val k = Random().nextInt(100)
            val array = ByteArray(1024 * k)
            KwFileUtils.fileWrite(File(App.getInstance().cacheDir, "fake-${UUID.randomUUID()}"), array)
        }
    }

}