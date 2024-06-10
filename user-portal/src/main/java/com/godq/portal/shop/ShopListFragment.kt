package com.godq.portal.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.godq.deeplink.DeepLinkUtils
import com.godq.portal.common.CommonTipView
import com.godq.portal.common.DataLoadingStateVm
import com.godq.portal.databinding.FragmentShopListLayoutBinding
import com.godq.portal.utils.ShopBillLatestDataRepo
import com.godq.resource.SkinTitleBarResDelegate
import com.lazylite.mod.widget.BaseFragment

class ShopListFragment : BaseFragment() {

    private var binding: FragmentShopListLayoutBinding? = null

    private val vm = ShopListMV()

    private val adapter = ShopListAdapter(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(vm)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopListLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.titleBar?.apply {
            setTitle("店铺列表")
            setVisible(showBack = false, showTitle = true)
            setResDelegate(SkinTitleBarResDelegate(this))
        }

        binding?.tipView?.onBtnClickListener = object : CommonTipView.OnBtnClickListener {
            override fun onBtnClick(currentTipType: Int) {
                when (currentTipType) {
                    DataLoadingStateVm.LOAD_TYPE_NOT_LOGIN -> {
                        DeepLinkUtils.load("test://open/account?type=login").execute()
                    }
                    else -> {
                        vm.requestShopList()
                    }
                }
            }
        }

        binding?.shopListRv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ShopListFragment.adapter
        }

        adapter.onItemClickListener =  BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
            (adapter?.data?.get(position) as? ShopEntity)?.let {
                vm.onItemClick(it)
                if (!ShopBillLatestDataRepo.isLatestData(it.id)) {
                    ShopBillLatestDataRepo.syncDataLatest(it.id)
                    adapter.notifyItemChanged(position)
                }
            }
        }

        vm.onShopListDataCallback = {
            adapter.setNewData(it)
        }

        vm.onShopBillLatestDataCallback = {
            adapter.notifyDataSetChanged()
        }

        vm.requestShopList()
    }
}