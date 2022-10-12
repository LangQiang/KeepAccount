package com.godq.portal.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.portal.databinding.FragmentMineLayoutBinding
import com.lazylite.mod.widget.BaseFragment

class MineFragment: BaseFragment() {

    private var binding: FragmentMineLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMineLayoutBinding.inflate(LayoutInflater.from(context))
        return binding?.root
    }
}