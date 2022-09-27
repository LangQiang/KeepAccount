package com.godq.keepaccounts.mgrbg.update

import androidx.databinding.ObservableField
import com.godq.keepaccounts.constants.getShopListUrl
import com.godq.keepaccounts.constants.getUpdateBillUrl
import com.godq.keepaccounts.shop.ShopEntity
import com.godq.keepaccounts.shop.parseShopList
import com.lazylite.mod.App
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.utils.toast.KwToast
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class BillUpdateVm {

    val shopId = ObservableField("")
    val date = ObservableField("")
    val bankAmount = ObservableField("0")
    val aliAmount = ObservableField("")
    val wxAmount = ObservableField("")
    val cashAmount = ObservableField("")
    val meituanAmount = ObservableField("")
    val douyinAmount = ObservableField("0")
    val waimaiAmount = ObservableField("0")
    val freeAmount = ObservableField("0")
    val tableTimes = ObservableField("")

    var onShopListDataCallback : ((List<ShopEntity>?) -> Unit)? = null


    fun onCommitClick() {

        if (invalid()) {
            KwToast.show("请补全信息")
            return
        }

        val header = HashMap<String, String>()
        header["Content-Type"] = "application/json"

        val json = JSONObject()
        json.putOpt("shop_id", shopId.get())
        json.putOpt("date", date.get())
        json.putOpt("opt_by", "GodQ")
        json.putOpt("table_times", tableTimes.get())
        val array = JSONArray()

        val bankJson = JSONObject()
        bankJson.putOpt("type", "银行卡")
        bankJson.putOpt("amount", bankAmount.get())
        array.put(bankJson)

        val aliJson = JSONObject()
        aliJson.putOpt("type", "支付宝")
        aliJson.putOpt("amount", aliAmount.get())
        array.put(aliJson)

        val wxJson = JSONObject()
        wxJson.putOpt("type", "微信")
        wxJson.putOpt("amount", wxAmount.get())
        array.put(wxJson)

        val cashJson = JSONObject()
        cashJson.putOpt("type", "现金")
        cashJson.putOpt("amount", cashAmount.get())
        array.put(cashJson)

        val mtJson = JSONObject()
        mtJson.putOpt("type", "美团")
        mtJson.putOpt("amount", meituanAmount.get())
        array.put(mtJson)

        val dyJson = JSONObject()
        dyJson.putOpt("type", "抖音")
        dyJson.putOpt("amount", douyinAmount.get())
        array.put(dyJson)

        val wmJson = JSONObject()
        wmJson.putOpt("type", "外卖")
        wmJson.putOpt("amount", waimaiAmount.get())
        array.put(wmJson)

        val freeJson = JSONObject()
        freeJson.putOpt("type", "免单")
        freeJson.putOpt("amount", freeAmount.get())
        array.put(freeJson)

        json.putOpt("type_list", array)


        val req = RequestInfo.newPost(getUpdateBillUrl(), header, json.toString().toByteArray())

        Timber.tag("commit").e(json.toString())

        KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
            if (it.code == 200) {
                FragmentOperation.getInstance().close()
                KwToast.show("上传成功！")
            }
        }
    }

    private fun invalid(): Boolean {
        return shopId.get().isNullOrEmpty()
                || date.get().isNullOrEmpty()
                || bankAmount.get().isNullOrEmpty()
                || aliAmount.get().isNullOrEmpty()
                || wxAmount.get().isNullOrEmpty()
                || cashAmount.get().isNullOrEmpty()
                || meituanAmount.get().isNullOrEmpty()
                || douyinAmount.get().isNullOrEmpty()
                || waimaiAmount.get().isNullOrEmpty()
                || freeAmount.get().isNullOrEmpty()
                || tableTimes.get().isNullOrEmpty()

    }

    fun onDateSelectClick() {
        App.getMainActivity()?.apply {
            with(DateSelectDialog(this)) {
                onSelectCallback = {
                    date.set(it)
                }
                showDialog()
            }
        }
    }

    fun requestShopList() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet(getShopListUrl())) {
            val data = it.dataToString()
            Timber.tag("shopppp").e(data)
            onShopListDataCallback?.invoke(parseShopList(data))
        }
    }
}