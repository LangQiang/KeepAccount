package com.godq.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.cms.asset.AssetListFragment
import com.godq.cms.databinding.FragmentBgMgrHomeLayoutBinding
import com.godq.deeplink.DeepLinkUtils
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.widget.BaseFragment
import org.json.JSONObject
import timber.log.Timber

class BillMgrHomeFragment: BaseFragment() {

    var binding: FragmentBgMgrHomeLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBgMgrHomeLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.mgrView = this
        return binding?.root
    }


    fun update() {
        DeepLinkUtils.load("test://open/cms/update").execute()
    }

    fun delete() {
        val data = binding?.deleteInputView?.text.toString()
        val json = JSONObject()
        json.putOpt("shop_id", 1)
        json.putOpt("date", data)
        val req = RequestInfo.newPost(deleteBillRecordUrl(), mapOf("Content-Type" to "application/json"), json.toString().toByteArray())
        KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
            Timber.tag("Mgr").e(it.dataToString())
        }
    }

    fun clickAssetBtn() {
        FragmentOperation.getInstance().showFullFragment(AssetListFragment())

        val header = HashMap<String, String>()
        header["Content-Type"] = "application/json"
        val json = JSONObject()
        json.putOpt("shop_name", "双门洞的夏天-乐松")
        json.putOpt("shop_img", "https://godq-1307306000.cos.ap-beijing.myqcloud.com/WechatIMG64.jpeg")
        json.putOpt("shop_desc", "韩式烤肉")
        json.putOpt("shop_addr", "三大动力路乐松商场内4层")
        json.putOpt("shop_phone", "18645013316")

        val req = RequestInfo.newPost("http://43.138.100.114/shop/create", header, json.toString().toByteArray())


        KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
            Timber.tag("createShop").e(it.dataToString())
        }
    }
}