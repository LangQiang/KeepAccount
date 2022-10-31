package com.godq.portal.common

import androidx.databinding.ObservableField

interface DataLoadingStateVm {
    companion object {
        const val LOAD_TYPE_UNKNOWN: Int = -1
        const val LOAD_TYPE_HAS_CONTENT: Int = 0
        const val LOAD_TYPE_NET_ERROR: Int = 1
        const val LOAD_TYPE_EMPTY: Int = 2
        const val LOAD_TYPE_LOADING: Int = 3
        const val LOAD_TYPE_NOT_LOGIN: Int = 4
    }
    val loadUIStateType: ObservableField<Int>
    fun setDataLoadingState(state: Int) {
        loadUIStateType.set(state)
    }
}