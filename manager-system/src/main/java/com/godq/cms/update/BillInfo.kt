package com.godq.cms.update

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.godq.cms.BR

class BillInfo: BaseObservable() {

    @get:Bindable
    var shopId: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.shopId)
        }

    @get:Bindable
    var date: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.date)
        }

    @get:Bindable
    var tableTimes: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.tableTimes)
        }

    @get:Bindable
    var bankAmount: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.bankAmount)
        }

    @get:Bindable
    var aliAmount: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.aliAmount)
        }

    @get:Bindable
    var wxAmount: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.wxAmount)
        }

    @get:Bindable
    var cashAmount: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.cashAmount)
        }

    @get:Bindable
    var meituanAmount: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.meituanAmount)
        }

    @get:Bindable
    var douyinAmount: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.douyinAmount)
        }

    @get:Bindable
    var waimaiAmount: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.waimaiAmount)
        }

    @get:Bindable
    var freeAmount: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.freeAmount)
        }

    @get:Bindable
    var payOut: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.payOut)
        }

    fun setInfoWithoutId(info: BillInfo) {
        this.date = info.date
        this.tableTimes = info.tableTimes
        this.bankAmount = info.bankAmount
        this.aliAmount = info.aliAmount
        this.wxAmount = info.wxAmount
        this.cashAmount = info.cashAmount
        this.meituanAmount = info.meituanAmount
        this.douyinAmount = info.douyinAmount
        this.waimaiAmount = info.waimaiAmount
        this.freeAmount = info.freeAmount
        this.payOut = info.payOut
    }

    fun invalid(): Boolean {
        return shopId.isNullOrEmpty()
                || date.isNullOrEmpty()
                || bankAmount.isNullOrEmpty()
                || aliAmount.isNullOrEmpty()
                || wxAmount.isNullOrEmpty()
                || cashAmount.isNullOrEmpty()
                || meituanAmount.isNullOrEmpty()
                || douyinAmount.isNullOrEmpty()
                || waimaiAmount.isNullOrEmpty()
                || freeAmount.isNullOrEmpty()
                || tableTimes.isNullOrEmpty()
                || payOut.isNullOrEmpty()

    }

    override fun toString(): String {
        return "BillInfo(shopId=$shopId, date=$date, tableTimes=$tableTimes, bankAmount=$bankAmount, aliAmount=$aliAmount, wxAmount=$wxAmount, cashAmount=$cashAmount, meituanAmount=$meituanAmount, douyinAmount=$douyinAmount, waimaiAmount=$waimaiAmount, freeAmount=$freeAmount, payOut=$payOut)"
    }


}