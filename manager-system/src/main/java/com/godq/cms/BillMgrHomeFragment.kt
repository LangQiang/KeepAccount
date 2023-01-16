package com.godq.cms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.cms.databinding.FragmentBgMgrHomeLayoutBinding
import com.godq.deeplink.DeepLinkUtils
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
}