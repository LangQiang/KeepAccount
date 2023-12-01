package com.godq.portal.billdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ObservableField
import com.godq.portal.constants.BILL_TOTAL_DIFF
import com.godq.portal.constants.getBillTotal
import com.godq.portal.databinding.BillDetailHeaderLayoutBinding
import com.godq.portal.ext.scale
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.utils.toast.KwToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


/**
 * @author  GodQ
 * @date  2023/9/22 22:20
 */
class BillDetailHeaderComponent(private val shopId: String?) {

    private var binding: BillDetailHeaderLayoutBinding? = null

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    var billTotal = ObservableField(0f)

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
    }

    fun onBillTotalClick() {
        KwToast.show("初始余额：$BILL_TOTAL_DIFF")
    }

}