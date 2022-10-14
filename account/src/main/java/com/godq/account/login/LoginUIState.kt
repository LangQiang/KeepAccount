package com.godq.account.login

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.godq.account.BR

class LoginUIState: BaseObservable() {

    companion object {
        const val PAGE_TYPE_DEFAULT = 0
        const val PAGE_TYPE_LOADING = 1
    }

    @get:Bindable
    var accountName:String? = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.accountName)
    }

    @get:Bindable
    var password: String? = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.password)
    }

    @get:Bindable
    var pageState: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.pageState)
        }
}