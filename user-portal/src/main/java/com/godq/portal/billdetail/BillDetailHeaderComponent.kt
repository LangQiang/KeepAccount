package com.godq.portal.billdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.godq.portal.constants.BILL_TOTAL_DIFF
import com.godq.portal.constants.getBillTotal
import com.godq.portal.databinding.BillDetailHeaderLayoutBinding
import com.godq.portal.ext.scale
import com.lazylite.mod.App
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.utils.toast.KwToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/9/22 22:20
 */
class BillDetailHeaderComponent(private val shopId: String?) {

    private var binding: BillDetailHeaderLayoutBinding? = null

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var billTotal = ObservableField(0f)

    var dateStart = ObservableField("2023-08-28")
    var dateEnd = ObservableField("2025-01-01")
    //毛利
    var grossProfit = ObservableField("毛利:0.0%")
    var foodPercent = ObservableField("食材:0.0%")
    var wEPercent = ObservableField("水电:0.0%")
    var otherPercent = ObservableField("其他:0.0%")

    fun getView(context: Context): View {
        val tBinding = BillDetailHeaderLayoutBinding.inflate(LayoutInflater.from(context))
        tBinding.headerComponent = this
        binding = tBinding
        requestData()
        return tBinding.root
    }

    private fun requestData() {
        scope.launch {
            val totalDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, "bill_total")))
                parseBillTotal(res.dataToString())
            }
            val payoutDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, "bill_pay_out")))
                parseBillTotal(res.dataToString())
            }
            val bonusDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, "bill_bonus")))
                parseBillTotal(res.dataToString())
            }
            val total = totalDeferred.await()
            val payout = payoutDeferred.await()
            val bonus = bonusDeferred.await()
            //余额=总流水-支出-分红+初始资金
            billTotal.set((total - payout - bonus + if (shopId == "2") BILL_TOTAL_DIFF else 0f).scale(2))
        }
        requestSubData()
    }

    private fun requestSubData(startDate:String = dateStart.get()?: "", endDate: String = dateEnd.get()?: "") {
        scope.launch {
            val totalDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, "bill_total", startDate = startDate, endDate = endDate)))
                parseBillTotal(res.dataToString())
            }
            val foodDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, subTypeName = "食材", startDate = startDate, endDate = endDate)))
                parseBillTotal(res.dataToString())
            }
            val shuiDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, subTypeName = "水费", startDate = startDate, endDate = endDate)))
                parseBillTotal(res.dataToString())
            }
            val dianDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, subTypeName = "电费", startDate = startDate, endDate = endDate)))
                parseBillTotal(res.dataToString())
            }
            val rentDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, subTypeName = "房租", startDate = startDate, endDate = endDate)))
                parseBillTotal(res.dataToString())
            }
            val otherDeferred = async(Dispatchers.IO) {
                val res = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getBillTotal(shopId, subTypeName = "其他支出", startDate = startDate, endDate = endDate)))
                parseBillTotal(res.dataToString())
            }
            val total = totalDeferred.await()
            val food = foodDeferred.await()
            val shui = shuiDeferred.await()
            val dian = dianDeferred.await()
            val rent = rentDeferred.await()
            val other = otherDeferred.await()
            Timber.tag("wangshuo").e("total:$total food:$food  shui:$shui dian:$dian rent:$rent other:$other")
            if (total == 0f)
                return@launch
            val foodNum = (food * 100 / total).scale(1)
            val wENum = ((shui + dian) * 100 / total).scale(1)
            val otherNum = (other * 100 / total).scale(1)
            val grossProfitNum = (100 - foodNum - wENum - otherNum).scale(1)
            foodPercent.set("食材:$foodNum%")
            wEPercent.set("水电:$wENum%")
            otherPercent.set("其他:$otherNum%")
            grossProfit.set("毛利:$grossProfitNum%")
        }
    }

    fun onBillTotalClick() {
        KwToast.show("初始余额：$BILL_TOTAL_DIFF")
    }

    fun onStartDateClick() {
        App.getMainActivity()?.apply {
            with(DateSelectDialog(this, dateStart.get())) {
                onSelectCallback = {
                    dateStart.set(it)
                    requestSubData()
                }
                showDialog()
            }
        }
    }

    fun onEndDateClick() {
        App.getMainActivity()?.apply {
            with(DateSelectDialog(this, dateEnd.get())) {
                onSelectCallback = {
                    dateEnd.set(it)
                    requestSubData()
                }
                showDialog()
            }
        }
    }

}