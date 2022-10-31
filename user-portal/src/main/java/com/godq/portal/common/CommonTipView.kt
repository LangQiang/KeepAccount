package com.godq.portal.common

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.godq.portal.R
import com.lazylite.mod.widget.loading.CommonLoadingView

class CommonTipView(mContext: Context, mAttributeSet: AttributeSet?, mDefStyleAttr: Int)
    : LinearLayout(mContext, mAttributeSet, mDefStyleAttr) {

    constructor(mContext: Context, mAttributeSet: AttributeSet?): this(mContext, mAttributeSet, 0)

    constructor(mContext: Context): this(mContext, null)

    private val rootTipContainer: View?
    private val imageTip: ImageView?
    private val topTextTip: TextView?
    private val jumpButton: TextView?

    private val loadingView: CommonLoadingView?

    private var currentTipType: Int = -1

    var onBtnClickListener: OnBtnClickListener? = null

    init {
        visibility = View.GONE
        gravity = Gravity.CENTER
        orientation = VERTICAL
        setBackgroundResource(R.color.main_bg)
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.common_tip_view_layout, this, true)
        rootTipContainer = findViewById(R.id.tip_root_view)
        imageTip = findViewById(R.id.image_tip)
        topTextTip = findViewById(R.id.top_text_tip)
        jumpButton = findViewById(R.id.jump_button)

        loadingView = findViewById(R.id.loading_view)

        jumpButton.setOnClickListener {
            onBtnClickListener?.onBtnClick(currentTipType)
        }
    }

    fun setRootTipContainerVisible(visible: Boolean) {
        rootTipContainer?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private var loadingViewOptId = 0

    fun setLoadingViewGone(isGone: Boolean, delay: Long) {
        loadingView ?: return
        val currentOpt = ++loadingViewOptId
        if (delay <= 0) {
            loadingView.visibility = if (isGone) GONE else VISIBLE
        } else {
            loadingView.postDelayed(Runnable {
                if (currentOpt != loadingViewOptId) {
                    return@Runnable
                }
                loadingView.visibility = if (isGone) GONE else VISIBLE
            }, delay)
        }
    }

    fun showTip(@DrawableRes imgId: Int, @StringRes topTextId: Int, @StringRes btnTextId: Int, tipType: Int) {
        currentTipType = tipType

        when (tipType) {
            DataLoadingStateVm.LOAD_TYPE_HAS_CONTENT -> {
                visibility = View.GONE
            }
            DataLoadingStateVm.LOAD_TYPE_NET_ERROR -> {
                visibility = View.VISIBLE
                setRootTipContainerVisible(true)
                setLoadingViewGone(true, 0)
            }
            DataLoadingStateVm.LOAD_TYPE_EMPTY -> {
                visibility = View.VISIBLE
                setRootTipContainerVisible(true)
                setLoadingViewGone(true, 0)
            }
            DataLoadingStateVm.LOAD_TYPE_LOADING -> {
                visibility = View.VISIBLE
                setRootTipContainerVisible(false)
                setLoadingViewGone(false, 500)
            }
            DataLoadingStateVm.LOAD_TYPE_NOT_LOGIN -> {
                visibility = View.VISIBLE
                setRootTipContainerVisible(true)
                setLoadingViewGone(true, 0)
            }
            else -> {
                visibility = View.GONE
            }
        }

        if (imgId != 0) {
            imageTip?.setImageResource(imgId)
            imageTip?.visibility = View.VISIBLE
        } else {
            imageTip?.visibility = View.GONE
        }

        if (topTextId != 0) {
            topTextTip?.setText(topTextId)
            topTextTip?.visibility = View.VISIBLE
        } else {
            topTextTip?.visibility = View.GONE
        }

        if (btnTextId != 0) {
            jumpButton?.setText(btnTextId)
            jumpButton?.visibility = View.VISIBLE
        } else {
            jumpButton?.visibility = View.GONE
        }
    }



    interface OnBtnClickListener {
        fun onBtnClick(currentTipType: Int)
    }
}