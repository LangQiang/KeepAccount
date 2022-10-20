package com.godq.portal.binding

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.godq.portal.R
import com.godq.portal.billdetail.BillEntity
import com.godq.portal.utils.HolidayRepo
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

