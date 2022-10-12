package com.godq.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.cms.databinding.FragmentBgMgrHomeLayoutBinding
import com.godq.deeplink.DeepLinkUtils
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.utils.toast.KwToast
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

        binding?.optComfirm?.setOnClickListener {
            val text = binding?.optEt?.text?.toString()
            if (text.isNullOrEmpty()) {
                KwToast.show("请输入合法操作人")
            } else {
                ConfMgr.setStringValue("", "opt_by", text, false)
                binding?.optComfirm?.visibility = View.GONE
                binding?.optEt?.visibility = View.GONE
            }
        }
        updateOptUI()
    }

    fun update() {
        val optBy = ConfMgr.getStringValue("", "opt_by", "")
        if (optBy.isNullOrEmpty()) {
            KwToast.show("未检测到操作人，无法上传")
        } else {
            DeepLinkUtils.load("test://open/cms/update").execute()
        }
        updateOptUI(optBy)
    }

    private fun updateOptUI(optBy: String? = ConfMgr.getStringValue("", "opt_by", "")) {
        if (optBy.isNullOrEmpty()) {
            binding?.optComfirm?.visibility = View.VISIBLE
            binding?.optEt?.visibility = View.VISIBLE
        } else {
            binding?.optComfirm?.visibility = View.GONE
            binding?.optEt?.visibility = View.GONE
        }
    }
}