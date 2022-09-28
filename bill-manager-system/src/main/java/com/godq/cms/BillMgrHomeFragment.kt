package com.godq.keepaccounts.mgrbg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.keepaccounts.GuideController
import com.godq.keepaccounts.MainActivity
import com.godq.keepaccounts.R
import com.godq.keepaccounts.databinding.FragmentBgMgrHomeLayoutBinding
import com.godq.keepaccounts.utils.jumpToBillUpdateFragment
import com.lazylite.mod.App
import com.lazylite.mod.widget.BaseFragment

class BillMgrHomeFragment: BaseFragment() {

    var binding: FragmentBgMgrHomeLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBgMgrHomeLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.mgrView = this
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.titleBar?.setRightTextBtn("切换")
        binding?.titleBar?.setRightColor(R.color.skin_official_blue)
        binding?.titleBar?.setRightListener {
            (App.getMainActivity() as? MainActivity)?.switchMode(GuideController.OPT_SWITCH)
        }
    }

    fun update() {
        jumpToBillUpdateFragment()
    }
}