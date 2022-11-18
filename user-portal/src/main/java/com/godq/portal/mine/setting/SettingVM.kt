package com.godq.portal.mine.setting

import com.godq.deeplink.DeepLinkUtils
import com.godq.portal.UserPortalLinkHelper
import com.godq.threadpool.ThreadPool
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.utils.KwFileUtils
import com.lazylite.mod.utils.toast.KwToast

class SettingVM {

    fun onUserInfoClick() {

    }

    fun onMgrCtrlClick() {
        DeepLinkUtils.load("test://open/cms/index").execute()
    }

    fun onHelpClick() {
        KwToast.show("去聊天室里说")
    }

    fun onAboutClick() {
        KwToast.show("我们都是拉磨的驴")
    }

    fun onCacheClick() {
        ThreadPool.exec {
            val cache = KwFileUtils.getDirSize(App.getInstance().cacheDir.absolutePath)
            App.getInstance().cacheDir.listFiles()?.apply {
                for (file in this) {
                    if (file.name.startsWith("fake-")) {
                        file.delete()
                    }
                }
            }
            val size = if (cache < 1024) {
                "${cache}B"
            } else if (cache < 1024 * 1024) {
                "${cache / 1024}KB"
            } else if (cache < 1024 * 1024 * 1024) {
                "${cache / 1024 / 1024}MB"
            } else {
                "${cache / 1024 / 1024 / 1024}GB"
            }
            KwToast.show("缓存清除成功：$size")
        }
    }

    fun onLogOutClick() {
        UserPortalLinkHelper.logout()
        if (FragmentOperation.getInstance().topFragment is SettingFragment) {
            FragmentOperation.getInstance().close()
        }
    }
}