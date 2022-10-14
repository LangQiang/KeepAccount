package com.godq.portal.mine

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.godq.portal.BR

class MineHomeUIState: BaseObservable() {

    companion object {
        const val DEFAULT_TITLE_NAME = "点击登录"
        const val DEFAULT_TITLE_ID = "id:未知"
    }

    @get:Bindable
    var mineHomeTitleName = DEFAULT_TITLE_NAME
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeTitleName)
        }

    @get:Bindable
    var mineHomeTitleID = DEFAULT_TITLE_ID
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeTitleID)
        }

    @get:Bindable
    var mineHomeTitleImg = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeTitleImg)
        }


}