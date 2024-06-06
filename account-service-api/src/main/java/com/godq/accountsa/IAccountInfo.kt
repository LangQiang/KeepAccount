package com.godq.accountsa

interface IAccountInfo {
    fun getUserId(): String
    fun getNickName(): String
    fun getToken(): String
    fun getAvatarUrl(): String
    fun isLogin(): Boolean

    fun toJson(): String
    fun updateInfo(nickName: String?, avatarUrl: String?)
}