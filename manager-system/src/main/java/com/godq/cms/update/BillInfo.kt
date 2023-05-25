package com.godq.cms.update

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.godq.cms.BR
import java.lang.Exception

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
    var otherAmount: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.otherAmount)
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

    @get:Bindable
    var payOutMaterials: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.payOutMaterials)
        }

    @get:Bindable
    var payOutLabor: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.payOutLabor)
        }

    @get:Bindable
    var payOutWater: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.payOutWater)
        }

    @get:Bindable
    var payOutElectricity: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.payOutElectricity)
        }

    @get:Bindable
    var payOutGas: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.payOutGas)
        }

    @get:Bindable
    var payOutDividends: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.payOutDividends)
        }

    @get:Bindable
    var payOutOther: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.payOutOther)
        }

    @get:Bindable
    var total: String? = "0"
        set(value) {
            field = value
            notifyPropertyChanged(BR.total)
        }

    fun setInfo(info: BillInfo, withoutId: Boolean = false) {
        if (!withoutId) {
            this.shopId = info.shopId
        }
        this.date = info.date
        this.tableTimes = info.tableTimes
        this.bankAmount = info.bankAmount
        this.aliAmount = info.aliAmount
        this.wxAmount = info.wxAmount
        this.cashAmount = info.cashAmount
        this.meituanAmount = info.meituanAmount
        this.douyinAmount = info.douyinAmount
        this.waimaiAmount = info.waimaiAmount
        this.otherAmount = info.otherAmount
        this.freeAmount = info.freeAmount
        this.payOut = info.payOut
        this.payOutMaterials = info.payOutMaterials
        this.payOutLabor = info.payOutLabor
        this.payOutWater = info.payOutWater
        this.payOutElectricity = info.payOutElectricity
        this.payOutGas = info.payOutGas
        this.payOutDividends = info.payOutDividends
        this.payOutOther = info.payOutOther
        this.total = info.total
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
                || otherAmount.isNullOrEmpty()
                || freeAmount.isNullOrEmpty()
                || tableTimes.isNullOrEmpty()
                || payOut.isNullOrEmpty()
                || payOutMaterials.isNullOrEmpty()
                || payOutLabor.isNullOrEmpty()
                || payOutWater.isNullOrEmpty()
                || payOutElectricity.isNullOrEmpty()
                || payOutGas.isNullOrEmpty()
                || payOutDividends.isNullOrEmpty()
                || payOutOther.isNullOrEmpty()
                || total.isNullOrEmpty()

    }

    fun checkTotal() = try {
        val bank = bankAmount?.toFloat() ?: 0f
        val ali = aliAmount?.toFloat() ?: 0f
        val wx = wxAmount?.toFloat() ?: 0f
        val cash = cashAmount?.toFloat() ?: 0f
        val mt = meituanAmount?.toFloat() ?: 0f
        val dy = douyinAmount?.toFloat() ?: 0f
        val wm = waimaiAmount?.toFloat() ?: 0f
        val other = otherAmount?.toFloat() ?: 0f
        bank + ali + wx + cash + mt + dy + wm + other == (total?.toFloat()?: 0f)
    } catch (e: Exception) {
        false
    }

    override fun toString(): String {
        return """BillInfo(
            shopId=$shopId, 
            date=$date, 
            tableTimes=$tableTimes, 
            bankAmount=$bankAmount, 
            aliAmount=$aliAmount, 
            wxAmount=$wxAmount, 
            cashAmount=$cashAmount, 
            meituanAmount=$meituanAmount, 
            douyinAmount=$douyinAmount, 
            waimaiAmount=$waimaiAmount, 
            otherAmount=${otherAmount}, 
            freeAmount=$freeAmount, 
            payOut=$payOut, 
            payOutMaterials=$payOutMaterials, 
            payOutLabor=$payOutLabor, 
            payOutWater=$payOutWater, 
            payOutElectricity=$payOutElectricity, 
            payOutGas=$payOutGas, 
            payOutDividends=$payOutDividends, 
            payOutOther=$payOutOther, 
            total=$total)"""

    }


}