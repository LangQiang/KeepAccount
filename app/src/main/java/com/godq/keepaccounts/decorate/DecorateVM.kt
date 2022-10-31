package com.godq.keepaccounts.decorate

import com.godq.accountsa.IAccountService
import com.godq.deeplink.DeepLinkUtils
import com.godq.im.PublicChatRoomManager
import com.lazylite.bridge.router.ServiceImpl
import com.lazylite.mod.messagemgr.MessageManager

class DecorateVM {

    var uiState = DecorateUIState()

    fun onChatClick() {
        DeepLinkUtils.load("test://open/chat/room").execute()
    }

    fun onAttach() {
        MessageManager.getInstance().attachMessage(IAccountService.IAccountObserver.EVENT_ID, object : IAccountService.IAccountObserver {
            override fun onLogin() {
                uiState.chatRoomVisible = true
            }

            override fun onLogout() {
                uiState.chatRoomVisible = false
            }

            override fun onUpdate() {

            }

        })

        uiState.chatRoomVisible = (ServiceImpl.getInstance().getService(IAccountService::class.java.name) as? IAccountService)?.isLogin() == true

        PublicChatRoomManager.setUnReadMsgTipListener(object : PublicChatRoomManager.UnReadMsgTipListener {
            override fun unReadMsg(count: Int) {
                MessageManager.getInstance().asyncRun(object :MessageManager.Runner() {
                    override fun call() {
                        uiState.chatUnReadCount = count
                    }
                })
            }

        })
    }
}