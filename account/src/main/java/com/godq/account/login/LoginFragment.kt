package com.godq.account.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.account.databinding.FragmentLoginLayoutBinding
import com.lazylite.mod.utils.SoftKeyboardHelper
import com.lazylite.mod.widget.BaseFragment

class LoginFragment: BaseFragment() {

    private var binding: FragmentLoginLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.vm = LoginVM()
        return binding?.root
    }

    override fun onStop() {
        super.onStop()
        SoftKeyboardHelper.hideKeyboard(binding?.root)
    }
}