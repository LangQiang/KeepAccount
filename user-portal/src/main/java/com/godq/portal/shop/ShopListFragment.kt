package com.godq.keepaccounts.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.godq.keepaccounts.GuideController
import com.godq.keepaccounts.MainActivity
import com.godq.keepaccounts.R
import com.godq.keepaccounts.databinding.FragmentShopListLayoutBinding
import com.lazylite.mod.App
import com.lazylite.mod.widget.BaseFragment

class ShopListFragment : BaseFragment() {

    private var binding: FragmentShopListLayoutBinding? = null

    private val vm = ShopListMV()

    private val adapter = ShopListAdapter(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopListLayoutBinding.inflate(LayoutInflater.from(context))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.titleBar?.setMainTitle("店铺列表")
        binding?.titleBar?.setRightTextBtn("切换")
        binding?.titleBar?.setRightColor(R.color.skin_official_blue)
        binding?.titleBar?.setRightListener {
            (App.getMainActivity() as? MainActivity)?.switchMode(GuideController.OPT_SWITCH)
        }
        binding?.shopListRv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ShopListFragment.adapter
        }

        adapter.onItemClickListener =  BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
            vm.onItemClick(adapter?.data?.get(position) as? ShopEntity)
        }

        vm.onShopListDataCallback = {
            adapter.setNewData(it)
        }

        vm.requestShopList()
    }
}