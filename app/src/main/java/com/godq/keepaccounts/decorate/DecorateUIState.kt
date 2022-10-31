package com.godq.keepaccounts.decorate

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.godq.keepaccounts.BR

class DecorateUIState: BaseObservable() {

    @get:Bindable
    var chatRoomVisible = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.chatRoomVisible)
        }

    @get:Bindable
    var chatUnReadCount = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.chatUnReadCount)
        }

}