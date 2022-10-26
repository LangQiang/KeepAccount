package com.godq.keepaccounts.decorate

import com.godq.deeplink.DeepLinkUtils

class DecorateVM {
    fun onChatClick() {
        DeepLinkUtils.load("test://open/chat/room").execute()
    }
}