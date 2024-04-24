package com.godq.portal.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.godq.portal.R
import com.godq.portal.billdetail.BillEntity
import com.godq.portal.billdetail.BillMonthEntity
import com.godq.portal.billdetail.BillTableListView
import com.godq.portal.billdetail.BillWeekEntity
import com.godq.portal.ext.scale
import com.godq.portal.utils.HolidayRepo
import com.godq.portal.utils.WeatherRepo
import com.lazylite.mod.App

@BindingAdapter("setBillItemBg")
fun setBillItemBackGround(view: View, billEntity: BillEntity) {
    //        android:background="@{itemData.expand?@drawable/ka_bill_item_bg2:@drawable/ka_bill_item_bg1}"
    val state = HolidayRepo.getHolidayState(billEntity.date) ?: HolidayRepo.STATE_WORK_DAY

    when (state) {
        HolidayRepo.STATE_LEGAL_HOLIDAY, HolidayRepo.STATE_REST_DAY -> if (billEntity.expand) R.drawable.ka_bill_item_rest_day_bg2 else R.drawable.ka_bill_item_rest_day_bg1
        else -> {
            if (billEntity.expand) R.drawable.ka_bill_item_bg2 else R.drawable.ka_bill_item_bg1
        }
    }.apply {
        view.background = ContextCompat.getDrawable(App.getInstance(), this)
    }
}

@BindingAdapter("setWeatherIcon")
fun setWeather(view: ImageView, billEntity: BillEntity) {
    var iconId = when (WeatherRepo.getWeather(billEntity.date)) {
        WeatherRepo.WEATHER_QING -> {R.drawable.ka_qing_icon}
        WeatherRepo.WEATHER_YIN -> {R.drawable.ka_yintian_icon}
        WeatherRepo.WEATHER_YU -> {R.drawable.ka_zhongyu_icon}
        WeatherRepo.WEATHER_YUN -> {R.drawable.ka_yun_icon}
        WeatherRepo.WEATHER_BING_BAO -> {R.drawable.ka_bingbao_icon}
        WeatherRepo.WEATHER_WU -> {R.drawable.ka_wu_icon}
        WeatherRepo.WEATHER_SHA_CHEN -> {R.drawable.ka_chen_icon}
        WeatherRepo.WEATHER_LEI -> {R.drawable.ka_lei_icon}
        WeatherRepo.WEATHER_XUE -> {R.drawable.ka_xue_icon}
        else -> {
            null
        }
    }
    if (iconId != null) {
        view.setImageDrawable(ContextCompat.getDrawable(App.getInstance(), iconId))
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("setTableListView")
fun setTableList(view: BillTableListView, list: List<Int>) {
    view.setTableListData(list)
}

@BindingAdapter("setWeekTableCount")
fun setWeekTableCount(view: TextView, weekEntity: BillWeekEntity) {
    val weekTableCountText = (weekEntity.totalTablesCount.toFloat() / weekEntity.daysOfThisWeek).scale(2).toString() + "\n(" + weekEntity.totalTablesCount + ")桌"
    view.text = weekTableCountText
}

@BindingAdapter("setAverageTableCountOfMonth")
fun setAverageTableCountOfMonth(view: TextView, monthEntity: BillMonthEntity) {
    if (monthEntity.daysCountOfMonth == 0) return
    val text = "平均:" + String.format("%.1f",  monthEntity.totalTablesCount.toDouble() / monthEntity.daysCountOfMonth) + "桌"
    view.text = text
}

@BindingAdapter("setAverageTurnoverOfMonth")
fun setAverageTurnoverOfMonth(view: TextView, monthEntity: BillMonthEntity) {
    if (monthEntity.daysCountOfMonth == 0) return
    val text = "日均:" + String.format("%.1f",  monthEntity.total / monthEntity.daysCountOfMonth)
    view.text = text
}

@BindingAdapter("setCostOfOneTableForMonth")
fun setCostOfOneTableForMonth(view: TextView, monthEntity: BillMonthEntity) {
    if (monthEntity.totalTablesCount == 0) return
    val text = "客单价:" + String.format("%.1f",  monthEntity.total / monthEntity.totalTablesCount)
    view.text = text
}



