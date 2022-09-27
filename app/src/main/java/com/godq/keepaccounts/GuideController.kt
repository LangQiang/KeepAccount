package com.godq.keepaccounts

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lazylite.mod.config.ConfMgr

class GuideController {

    companion object {
        const val TYPE_USER = 1
        const val TYPE_MGR = -1
        const val TYPE_UNKNOWN = 0

        const val OPT_INIT = "init"
        const val OPT_SWITCH = "switch"

        const val CONF_KEY = "guide_choose"
    }

    private var containerRootView: View? = null

    private var chooseCallback: ((Int) -> Unit)? = null

    fun attachAndChoose(container: FrameLayout?, opt:String, callback: (Int) -> Unit) {

        container?: return

        container.removeAllViews()

        containerRootView = container

        chooseCallback = callback

        val choose = ConfMgr.getIntValue("", CONF_KEY, TYPE_UNKNOWN)
        if (opt == OPT_INIT || choose == TYPE_UNKNOWN) {
            when (choose) {
                TYPE_UNKNOWN -> {
                    setRootViewGone(false)
                    attachView(container)
                }
                else -> {
                    setRootViewGone(true)
                    callback(choose)
                }
            }
        } else {
            setRootViewGone(true)
            ConfMgr.setIntValue("", CONF_KEY, -choose, false)
            callback(-choose)
        }
    }

    private fun attachView(container: FrameLayout) {
        val guideRootView = View.inflate(container.context, R.layout.view_main_guide_layout, null)
        onViewCreated(guideRootView)
        container.addView(guideRootView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private fun onViewCreated(guideRootView: View?) {
        guideRootView?.findViewById<View>(R.id.guide_user_tv)?.setOnClickListener {
            ConfMgr.setIntValue("", CONF_KEY, TYPE_USER, false)
            chooseCallback?.invoke(TYPE_USER)
            setRootViewGone(true)
        }
        guideRootView?.findViewById<View>(R.id.guide_mgr_tv)?.setOnClickListener {
            ConfMgr.setIntValue("", CONF_KEY, TYPE_MGR, false)
            chooseCallback?.invoke(TYPE_MGR)
            setRootViewGone(true)
        }
    }

    private fun setRootViewGone(gone: Boolean) {
        containerRootView?.visibility = if (gone) View.GONE else View.VISIBLE
    }

}