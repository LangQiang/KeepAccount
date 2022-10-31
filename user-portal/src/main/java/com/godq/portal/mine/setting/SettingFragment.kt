package com.godq.portal.mine.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.portal.databinding.FragmentSettingLayoutBinding
import com.lazylite.mod.widget.BaseFragment

class SettingFragment: BaseFragment() {

    var binding: FragmentSettingLayoutBinding? = null

    val vm = SettingVM()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.title?.setBackListener {
            close()
        }
    }
}