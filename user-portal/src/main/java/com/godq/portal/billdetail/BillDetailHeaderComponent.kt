package com.godq.portal.billdetail

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ObservableField
import com.godq.portal.constants.getBillTotal
import com.godq.portal.databinding.BillDetailHeaderLayoutBinding
import com.godq.portal.ext.scale
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal


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
            val total = totalDeferred.await()
            val payout = payoutDeferred.await()
            billTotal.set((total - payout + if (shopId == "2") 92878.91f else 0f).scale(2))
        }
    }

}