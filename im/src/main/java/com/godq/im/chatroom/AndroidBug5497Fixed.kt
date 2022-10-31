package com.godq.im.chatroom

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

class AndroidBug5497Fixed {

    private val rect = Rect()

    private var changedView: View? = null
    private var contentView: View? = null

    private var lastHeight = 0

    var onChangedListener: (()->Unit)? = null

    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val height = computeUsableHeight()
        if (lastHeight == height) return@OnGlobalLayoutListener
        lastHeight = height
        changedView?.layoutParams?.height = height
        changedView?.requestLayout()

        onChangedListener?.invoke()
    }

    fun assistRootView(activity: Activity?, changedView: View?) {
        this.changedView = changedView
        this.contentView = activity?.findViewById(android.R.id.content)
        this.changedView?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)

    }


    private fun computeUsableHeight(): Int {
        this.contentView?.getWindowVisibleDisplayFrame(rect)
        return rect.bottom
    }

    fun release() {
        this.changedView?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)

    }
}