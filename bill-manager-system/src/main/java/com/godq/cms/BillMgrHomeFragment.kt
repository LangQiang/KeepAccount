package com.godq.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.cms.databinding.FragmentBgMgrHomeLayoutBinding
import com.godq.deeplink.DeepLinkUtils
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
            DeepLinkUtils.load("test://open/main/switch").execute()
        }
    }

    fun update() {
        DeepLinkUtils.load("test://open/cms/update").execute()
    }
}