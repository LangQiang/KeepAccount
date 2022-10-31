package com.godq.accountsa

import com.lazylite.mod.messagemgr.EventId
import com.lazylite.mod.messagemgr.IObserverBase

interface IAccountService {

//    companion object {
//        const val STATE_LOGIN = 0
//        const val STATE_LOGOUT = 1
//    }

    fun isLogin():Boolean
    fun getAccountInfo(): IAccountInfo
    fun updateAvatar(avatarUrl: String)
    fun logout()
//    fun registerOnAccountStateChangeListener(listener: OnAccountStateChangeListener)
//    fun unRegisterOnAccountStateChangeListener(listener: OnAccountStateChangeListener)

//    interface OnAccountStateChangeListener {
//        fun stateChanged(state: Int)
//    }

    interface IAccountObserver: IObserverBase {
        companion object {
            val EVENT_ID = EventId { IAccountObserver::class.java}
        }
        fun onLogin()
        fun onLogout()
        fun onUpdate()
    }
}