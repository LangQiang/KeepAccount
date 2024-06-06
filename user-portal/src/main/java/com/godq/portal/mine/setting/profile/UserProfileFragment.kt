package com.godq.portal.mine.setting.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.portal.databinding.FragmentUserProfileBinding
import com.lazylite.mod.widget.BaseFragment


/**
 * @author  GodQ
 * @date  2024/6/6 13:36
 */
class UserProfileFragment: BaseFragment() {
    var binding: FragmentUserProfileBinding? = null

    val vm = UserProfileVM()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.init()
        binding?.title?.setBackListener {
            close()
        }
    }
}