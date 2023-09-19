package com.godq.portal.billdetail

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow

class BillListTypeSelectPopView(mContext: Context, val list: List<String>) : ListPopupWindow(mContext) {

    init {
        height = WRAP_CONTENT
        width = 200
        isModal = true
        initView(mContext)
        setBackgroundDrawable(ColorDrawable(0xfff5f5f5.toInt()))
    }

    private fun initView(mContext: Context) {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, list)
        setAdapter(adapter)
    }
}