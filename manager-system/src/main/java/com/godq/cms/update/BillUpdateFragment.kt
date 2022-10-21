package com.godq.cms.update

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.godq.cms.R
import com.godq.cms.databinding.FragmentBillUpdateLayoutBinding
import com.lazylite.mod.widget.BaseFragment

class BillUpdateFragment: BaseFragment() {

    var binding: FragmentBillUpdateLayoutBinding? = null

    private val vm = BillUpdateVm()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillUpdateLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.titleBar?.setMainTitle("上传")

        binding?.niceSpinner?.apply {
            setSpinnerTextFormatter { item -> SpannableString((item as? BillShopEntity)?.name ?: "未知") }
            setSelectedTextFormatter { item -> SpannableString((item as? BillShopEntity)?.name ?: "未知") }
            setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            setOnSpinnerItemSelectedListener { parent, _, position, _ ->
                vm.billInfo.shopId = (parent.getItemAtPosition(position) as? BillShopEntity)?.id ?: ""
            }
        }

        vm.onShopListDataCallback = {
            if (!it.isNullOrEmpty()) {
                vm.billInfo.shopId = it[0].id
                binding?.niceSpinner?.attachDataSource(it)
            }
        }

        vm.requestShopList()
        binding?.root?.post {
            vm.initInfoFromClipBoard()
        }
    }

}