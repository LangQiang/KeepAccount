package com.godq.cms.update

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import com.godq.cms.getShopListUrl
import com.godq.cms.getUpdateBillUrl
import com.godq.msa.IManagerSystemObserver
import com.lazylite.mod.App
import com.lazylite.mod.config.ConfMgr
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.messagemgr.MessageManager
import com.lazylite.mod.utils.toast.KwToast
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class BillUpdateVm {

    val billInfo = BillInfo()

    var onShopListDataCallback : ((List<BillShopEntity>?) -> Unit)? = null


    fun onCommitClick() {

        val block = {
            val header = HashMap<String, String>()
            header["Content-Type"] = "application/json"

            val json = JSONObject()
            json.putOpt("shop_id", billInfo.shopId)
            json.putOpt("date", billInfo.date)
            json.putOpt("opt_by", ConfMgr.getStringValue("", "opt_by", ""))
            json.putOpt("table_times", billInfo.tableTimes)
            json.putOpt("pay_out", billInfo.payOut)
            json.putOpt("total", billInfo.total)
            json.putOpt("bonus", billInfo.bonus)

            val array = JSONArray()

            val payOutMTExtJson = JSONObject()
            payOutMTExtJson.putOpt("type", "美团扣点")
            payOutMTExtJson.putOpt("amount", billInfo.payOutMTExt)
            array.put(payOutMTExtJson)

            val payOutDYExtJson = JSONObject()
            payOutDYExtJson.putOpt("type", "抖音扣点")
            payOutDYExtJson.putOpt("amount", billInfo.payOutDYExt)
            array.put(payOutDYExtJson)

            val payOutMaterialsJson = JSONObject()
            payOutMaterialsJson.putOpt("type", "食材")
            payOutMaterialsJson.putOpt("amount", billInfo.payOutMaterials)
            array.put(payOutMaterialsJson)

            val payOutLaborJson = JSONObject()
            payOutLaborJson.putOpt("type", "人工")
            payOutLaborJson.putOpt("amount", billInfo.payOutLabor)
            array.put(payOutLaborJson)

            val payOutRentJson = JSONObject()
            payOutRentJson.putOpt("type", "房租")
            payOutRentJson.putOpt("amount", billInfo.payOutRent)
            array.put(payOutRentJson)

            val payOutWaterJson = JSONObject()
            payOutWaterJson.putOpt("type", "水费")
            payOutWaterJson.putOpt("amount", billInfo.payOutWater)
            array.put(payOutWaterJson)

            val payOutElectricityJson = JSONObject()
            payOutElectricityJson.putOpt("type", "电费")
            payOutElectricityJson.putOpt("amount", billInfo.payOutElectricity)
            array.put(payOutElectricityJson)

            val payOutGasJson = JSONObject()
            payOutGasJson.putOpt("type", "燃气")
            payOutGasJson.putOpt("amount", billInfo.payOutGas)
            array.put(payOutGasJson)

            val payOutOtherJson = JSONObject()
            payOutOtherJson.putOpt("type", "其他支出")
            payOutOtherJson.putOpt("amount", billInfo.payOutOther)
            array.put(payOutOtherJson)

            val bankJson = JSONObject()
            bankJson.putOpt("type", "银行卡")
            bankJson.putOpt("amount", billInfo.bankAmount)
            array.put(bankJson)

            val aliJson = JSONObject()
            aliJson.putOpt("type", "支付宝")
            aliJson.putOpt("amount", billInfo.aliAmount)
            array.put(aliJson)

            val wxJson = JSONObject()
            wxJson.putOpt("type", "微信")
            wxJson.putOpt("amount", billInfo.wxAmount)
            array.put(wxJson)

            val cashJson = JSONObject()
            cashJson.putOpt("type", "现金")
            cashJson.putOpt("amount", billInfo.cashAmount)
            array.put(cashJson)

            val mtJson = JSONObject()
            mtJson.putOpt("type", "美团")
            mtJson.putOpt("amount", billInfo.meituanAmount)
            array.put(mtJson)

            val mtVoucherJson = JSONObject()
            mtVoucherJson.putOpt("type", "美团代金券")
            mtVoucherJson.putOpt("amount", billInfo.meituanVoucherAmount)
            array.put(mtVoucherJson)

            val mtPackageJson = JSONObject()
            mtPackageJson.putOpt("type", "美团套餐")
            mtPackageJson.putOpt("amount", billInfo.meituanPackageAmount)
            array.put(mtPackageJson)

            val dyJson = JSONObject()
            dyJson.putOpt("type", "抖音")
            dyJson.putOpt("amount", billInfo.douyinAmount)
            array.put(dyJson)

            val wmJson = JSONObject()
            wmJson.putOpt("type", "外卖")
            wmJson.putOpt("amount", billInfo.waimaiAmount)
            array.put(wmJson)

            val otherJson = JSONObject()
            otherJson.putOpt("type", "其他")
            otherJson.putOpt("amount", billInfo.otherAmount)
            array.put(otherJson)

            val freeJson = JSONObject()
            freeJson.putOpt("type", "免单")
            freeJson.putOpt("amount", billInfo.freeAmount)
            array.put(freeJson)

            json.putOpt("type_list", array)


            val req = RequestInfo.newPost(getUpdateBillUrl(), header, json.toString().toByteArray())

            Timber.tag("commit").e(json.toString())

            KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
                if (it.code == 200) {
                    FragmentOperation.getInstance().close()
                    MessageManager.getInstance().asyncNotify(IManagerSystemObserver.EVENT_ID, object : MessageManager.Caller<IManagerSystemObserver>() {
                        override fun call() {
                            ob.onBillUpdate()
                        }

                    })
                    KwToast.show("上传成功！")
                }
            }
        }

        if (invalid()) {
            KwToast.show("请补全信息")
            return
        }

        if (!billInfo.checkTotal()) {
            App.getMainActivity()?.also {
                with(AlertDialog.Builder(it)) {
                    setTitle("提醒")
                    setMessage("总流水和收入不相等，是否重新核对？")
                    setPositiveButton("忽略") { _, _ ->
                        block()
                    }
                    setNegativeButton("重新核对") { _, _ ->

                    }
                    create().show()
                }
                return
            }
        }

        block()
    }

    private fun invalid(): Boolean {
        return billInfo.invalid()
    }

    fun onDateSelectClick() {
        App.getMainActivity()?.apply {
            with(DateSelectDialog(this, billInfo.date)) {
                onSelectCallback = {
                    billInfo.date = it
                }
                showDialog()
            }
        }
    }

    fun requestShopList() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet(getShopListUrl())) {
            val data = it.dataToString()
            Timber.tag("shopppp").e("url:${it.finalRequestUrl}\n$data")
            onShopListDataCallback?.invoke(parseShopList(data))
        }
    }

    fun initInfoFromClipBoard() {
        val clipBoard = (App.getInstance().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager) ?: return

        if (clipBoard.hasPrimaryClip() && clipBoard.primaryClip?.itemCount ?: 0 > 0) {
            val text = clipBoard.primaryClip?.getItemAt(0)?.text ?: return
            initInfo(text.toString())
        }
    }


    private fun initInfo(text: String) {
        formatBillInfoFromClipBoard(text)?.apply {
            val activity = App.getMainActivity()?: return@apply
            with(AlertDialog.Builder(activity)) {
                setTitle("提醒")
                setMessage("检测到剪切板中含有营业额报表信息，是否自动录入？")
                setPositiveButton("确认录入") { _, _ ->
                    billInfo.setInfo(this@apply)
                }
                setNegativeButton("取消") { _, _ ->

                }
                create().show()
            }
        }
    }
}