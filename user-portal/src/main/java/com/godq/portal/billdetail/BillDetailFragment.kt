package com.godq.portal.billdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.godq.portal.R
import com.godq.statisticwidget.histogram.HistogramView
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.widget.BaseFragment
import com.lazylite.mod.widget.KwTitleBar

class BillDetailFragment : BaseFragment() {

    private var shopId: String? = null

    private val adapter = BillDetailAdapter(null)

    private val vm = BillDetailMV()

    private var completeView: TextView? = null

    private var recyclerView: RecyclerView? = null

    private var histogramView: HistogramView? = null

    private var currentListType: String? = null

    private val listTypes = listOf(LIST_TYPE_DAY, LIST_TYPE_WEEK, LIST_TYPE_MONTH)

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
        const val LIST_TYPE_DAY = "日"
        const val LIST_TYPE_WEEK = "周"
        const val LIST_TYPE_MONTH = "月"
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentListType = ConfMgr.getStringValue("", "bill_list_type", "日")

        val titleBar = view.findViewById<KwTitleBar>(R.id.title_bar)
        titleBar.setMainTitle("账单")
        titleBar.setBackListener { close() }
        completeView = titleBar?.complete?.apply {
            text = currentListType ?: "未知"
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.black60))
        }
        titleBar?.setRightListener {
            val con = context ?: return@setRightListener
            with(BillListTypeSelectPopView(con, listTypes)) {
                setOnItemClickListener { _, _, position, _ ->
                    val listType = listTypes[position]
                    currentListType = listType
                    completeView?.text = listType
                    ConfMgr.setStringValue("", "bill_list_type", listType, false)
                    vm.changeListType(shopId, listType)
                    dismiss()
                }
                anchorView = completeView
                horizontalOffset = -200
                verticalOffset = 30
                show()
            }
        }
        histogramView = view.findViewById(R.id.histogram_view)

        recyclerView = view.findViewById(R.id.bill_detail_rv)
        recyclerView?.clipToPadding = false
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter

        adapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
                vm.onItemClick(adapter, position)
            }

        vm.onBillListDataCallback = {
            adapter.setNewData(it)
//            histogramView?.data = it
        }

        vm.onHolidayCallback = {
            adapter.notifyDataSetChanged()
        }

        vm.requestShopList(shopId, currentListType)
    }

    override fun isNeedSwipeBack(): Boolean {
        return false
    }
}