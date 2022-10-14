package com.godq.portal.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.portal.databinding.FragmentMineLayoutBinding
import com.lazylite.mod.widget.BaseFragment

class MineHomeFragment: BaseFragment() {

    private var binding: FragmentMineLayoutBinding? = null

    private val vm = MineHomeVM()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMineLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.updateLoginUI()
    }
}