package com.godq.cms.binding

import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import com.godq.cms.R
import com.godq.cms.procure.detail.EquipmentEntity


/**
 * @author  GodQ
 * @date  2023/4/27 3:48 下午
 */

@BindingAdapter("setEquipmentState")
fun setEquipmentState(view: View, state: Int) {
    val id = when(state) {
        EquipmentEntity.STATE_TODO -> {
            R.drawable.mgr_sys_todo_icon
        }
        EquipmentEntity.STATE_DISCARD -> {
            R.drawable.mgr_sys_discard_icon
        }
        else -> {
            R.drawable.mgr_sys_complete_icon
        }
    }
    view.setBackgroundResource(id)
}

@BindingAdapter("setViewVisible")
fun setViewVisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

