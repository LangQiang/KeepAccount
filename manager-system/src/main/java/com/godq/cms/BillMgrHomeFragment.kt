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


    fun update() {
        DeepLinkUtils.load("test://open/cms/update").execute()
    }
}