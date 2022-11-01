package com.godq.im.chatroom

import androidx.databinding.BindingAdapter
import com.lazylite.mod.widget.SmoothCircleProgressBar


@BindingAdapter("setSmoothCircleProgress")
fun setSCProgress(view: SmoothCircleProgressBar, progress: Double) {
    view.setProgress(progress.toFloat())
}