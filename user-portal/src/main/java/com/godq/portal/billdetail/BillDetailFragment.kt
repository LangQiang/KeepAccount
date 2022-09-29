package com.godq.portal.billdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.godq.portal.R
import com.lazylite.mod.widget.BaseFragment
import com.lazylite.mod.widget.KwTitleBar

class BillDetailFragment : BaseFragment() {

    private var shopId: String? = null

    private val adapter = BillDetailAdapter(null)

    private val vm = BillDetailMV()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(context, R.layout.fragment_bill_detail_layout, null)
    }

    companion object {
        fun getInstance(shopId: String): BillDetailFragment {
            val fragment = BillDetailFragment()
            fragment.shopId = shopId
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val titleBar = view.findViewById<KwTitleBar>(R.id.title_bar)
        titleBar.setMainTitle("流水")
        titleBar.setBackListener { close() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.bill_detail_rv)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        adapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
                vm.onItemClick(adapter, position)
            }

        vm.onBillListDataCallback = {
            adapter.setNewData(it)
        }

        vm.requestShopList(shopId)
    }
}