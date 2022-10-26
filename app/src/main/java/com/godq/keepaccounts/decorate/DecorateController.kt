package com.godq.keepaccounts.decorate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.godq.keepaccounts.databinding.KaDecorateLayerLayoutBinding

class DecorateController {

    private var binding: KaDecorateLayerLayoutBinding? = null

    private var containerView: FrameLayout? = null

    private var vm = DecorateVM()

    fun attach(rootView: FrameLayout?) {
        this.containerView = rootView ?: return
        val context = containerView?.context ?: return
        onCreateView(context)?.apply {
            containerView?.addView(this, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            onViewCreated(this)
        }
    }

    private fun onCreateView(context: Context): View? {
        binding = KaDecorateLayerLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }

    private fun onViewCreated(view: View) {
        //
    }

}