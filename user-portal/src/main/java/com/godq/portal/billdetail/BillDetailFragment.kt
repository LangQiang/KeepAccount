package com.godq.portal.billdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.godq.deeplink.DeepLinkUtils
import com.godq.portal.R
import com.godq.statisticwidget.histogram.HistogramView
import com.lazylite.mod.widget.BaseFragment
import com.lazylite.mod.widget.KwTitleBar

class BillDetailFragment : BaseFragment() {

    private var shopId: String? = null

    private val adapter = BillDetailAdapter(null)

    private val vm = BillDetailMV()

    private var completeView: TextView? = null

    private var recyclerView: RecyclerView? = null

    private var histogramView: HistogramView? = null

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
        completeView = titleBar?.complete?.apply {
            text = "文本"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.black60))
        }
        titleBar?.setRightListener {
            if ("图表" == completeView?.text) {
                completeView?.text = "文本"
                recyclerView?.visibility = View.VISIBLE
                histogramView?.visibility = View.INVISIBLE
            } else {
                completeView?.text = "图表"
                recyclerView?.visibility = View.INVISIBLE
                histogramView?.visibility = View.VISIBLE
            }
        }
        histogramView = view.findViewById(R.id.histogram_view)

        recyclerView = view.findViewById<RecyclerView>(R.id.bill_detail_rv)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter

        adapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
                vm.onItemClick(adapter, position)
            }

        vm.onBillListDataCallback = {
            adapter.setNewData(it)
            histogramView?.data = it
        }

        vm.requestShopList(shopId)
    }

    override fun isNeedSwipeBack(): Boolean {
        return false
    }
}