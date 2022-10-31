package com.godq.portal.mine.setting

import com.godq.deeplink.DeepLinkUtils
import com.godq.portal.UserPortalLinkHelper
import com.lazylite.mod.fragmentmgr.FragmentOperation

class SettingVM {

    fun onUserInfoClick() {

    }

    fun onMgrCtrlClick() {
        DeepLinkUtils.load("test://open/cms/index").execute()
    }

    fun onHelpClick() {

    }

    fun onAboutClick() {

    }

    fun onCacheClick() {

    }

    fun onLogOutClick() {
        UserPortalLinkHelper.logout()
        if (FragmentOperation.getInstance().topFragment is SettingFragment) {
            FragmentOperation.getInstance().close()
        }
    }
}