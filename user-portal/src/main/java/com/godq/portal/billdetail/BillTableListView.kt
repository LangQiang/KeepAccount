package com.godq.portal.billdetail

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BillTableListView(mContext: Context, mAttributeSet: AttributeSet?, mDefStyleAttr: Int)
    : View(mContext, mAttributeSet, mDefStyleAttr) {

    var list: List<Int>? = null

    var paint: Paint = Paint()

    private var rect: RectF = RectF()


    init {
        paint = Paint()
        paint.isAntiAlias = true
        paint.color = 0x30e3c887.toInt()
    }

    constructor(mContext: Context, mAttributeSet: AttributeSet?): this(mContext, mAttributeSet, 0)

    constructor(mContext: Context): this(mContext, null)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val tList = list?: return
        val y = height.toFloat()
        val interval = width.toFloat() / (tList.size + 1)
        var x = 0f
        for (i in tList) {
            x += interval
            rect.set(x, y * (1 - i / 100f), x + 10, y)
            canvas?.drawRect(rect, paint)
        }
    }

    fun setTableListData(list: List<Int>) {
        this.list = list
        invalidate()
    }
}