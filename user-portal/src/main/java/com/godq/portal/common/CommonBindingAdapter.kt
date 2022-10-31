package com.godq.portal.common

import android.view.View
import androidx.databinding.BindingAdapter
import com.godq.portal.R


@BindingAdapter("setLoadState")
fun showTipView(view: CommonTipView, loadType: Int) {
    when (loadType) {
        DataLoadingStateVm.LOAD_TYPE_HAS_CONTENT -> {
            view.visibility = View.GONE
        }
        DataLoadingStateVm.LOAD_TYPE_NET_ERROR -> {
            view.visibility = View.VISIBLE
            view.setRootTipContainerVisible(true)
            view.setLoadingViewGone(true, 0)
            view.showTip(R.drawable.base_list_empty, R.string.search_result_search_noconnect_tip, R.string.base_try, loadType)
        }
        DataLoadingStateVm.LOAD_TYPE_EMPTY -> {
            view.visibility = View.VISIBLE
            view.setRootTipContainerVisible(true)
            view.setLoadingViewGone(true, 0)
            view.showTip(R.drawable.base_list_empty, R.string.search_result_search_nocontent_tip, 0, loadType)
        }
        DataLoadingStateVm.LOAD_TYPE_LOADING -> {
            view.visibility = View.VISIBLE
            view.setRootTipContainerVisible(false)
            view.setLoadingViewGone(false, 500)
        }
        DataLoadingStateVm.LOAD_TYPE_NOT_LOGIN -> {
            view.visibility = View.VISIBLE
            view.setRootTipContainerVisible(true)
            view.setLoadingViewGone(true, 0)
            view.showTip(R.drawable.base_list_empty, R.string.tip_not_login_desc_str, R.string.tip_not_login_btn_str, loadType)
        }
        else -> {
            view.visibility = View.GONE
        }
    }
}
