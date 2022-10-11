package com.godq.account.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.account.databinding.FragmentRegisterLayoutBinding
import com.lazylite.mod.widget.BaseFragment

class RegistrationFragment: BaseFragment() {

    private var binding: FragmentRegisterLayoutBinding? = null
    private var vm: RegistrationVM? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = RegistrationVM()
        binding = FragmentRegisterLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}