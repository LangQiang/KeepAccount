package com.godq.cms.update

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import com.godq.cms.BR
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
            setSpinnerTextFormatter { item ->
                val text = (item as? BillShopEntity)?.name ?: item.toString()
                SpannableString(text).apply {
                    val colorSpan = ForegroundColorSpan(0xcc000000.toInt())
                    setSpan(colorSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
            setSelectedTextFormatter { item ->
                SpannableString((item as? BillShopEntity)?.name ?: item.toString())
            }
            setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
            setTextColor(0xcc000000.toInt())
            setOnSpinnerItemSelectedListener { parent, _, position, _ ->
                vm.billInfo.shopId = (parent.getItemAtPosition(position) as? BillShopEntity)?.id ?: ""
            }
        }

        vm.onShopListDataCallback = {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                vm.billInfo.shopId = list[0].id
                binding?.niceSpinner?.attachDataSource(list)
                binding?.niceSpinner?.selectedIndex = 0
            }
        }

        vm.requestShopList()
        binding?.root?.post {
            vm.initInfoFromClipBoard()
        }
    }

    private val list = ArrayList<BillShopEntity>()
    private val callback = object : OnPropertyChangedCallback(){
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId == BR.shopId) {
                for (i in list.indices) {
                    if (list[i].id == vm.billInfo.shopId) {
                        binding?.niceSpinner?.selectedIndex = i
                        return
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.billInfo.addOnPropertyChangedCallback(callback)
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.billInfo.removeOnPropertyChangedCallback(callback)
    }

}