package com.godq.portal.mine

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.godq.portal.BR

class MineHomeDataUIState: BaseObservable() {

    @get:Bindable
    var mineHomeTotalTurnover = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeTotalTurnover)
        }

    @get:Bindable
    var mineHomeLastDayTurnover = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeLastDayTurnover)
        }



    @get:Bindable
    var mineHomeCurrentMonthTurnover = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeCurrentMonthTurnover)
        }

    @get:Bindable
    var mineHomeLastMonthTurnover = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeLastMonthTurnover)
        }


    @get:Bindable
    var mineHomeCurrentWeekTurnover = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeCurrentWeekTurnover)
        }

    @get:Bindable
    var mineHomeLastWeekTurnover = 0.0
        set(value) {
            field = value
            notifyPropertyChanged(BR.mineHomeLastWeekTurnover)
        }

}