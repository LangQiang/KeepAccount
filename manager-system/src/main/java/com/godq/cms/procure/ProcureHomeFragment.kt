package com.godq.cms.procure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.godq.cms.databinding.ProcureHomeFragmentLayoutBinding
import com.lazylite.mod.widget.BaseFragment

class ProcureHomeFragment: BaseFragment() {

    var binding: ProcureHomeFragmentLayoutBinding? = null

    private var mAdapter = ProcureHomeAdapter(null)

    val vm = ProcureHomeVm()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProcureHomeFragmentLayoutBinding.inflate(LayoutInflater.from(context))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.titleBar?.apply {
            setMainTitle("采购")
            setRightTextBtn("新建")
            setRightListener {
                vm.create()
            }
        }

        binding?.procureRv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        mAdapter.setOnItemClickListener { adapter, view, position ->
            (adapter.data[position] as? ProcureEntity)?.apply {
                vm.onClickItem(this)
            }
        }

        vm.onDateCallback = { type, data ->
            if (type == ProcureHomeVm.REQ_TYPE_INIT) {
                mAdapter.setNewData(data)
            } else {

            }
        }

        vm.requestProcureList()
    }





//    fun update() {
//        DeepLinkUtils.load("test://open/cms/update").execute()
//    }

//    fun delete() {
//        val data = binding?.deleteInputView?.text.toString()
//        val json = JSONObject()
//        json.putOpt("shop_id", 1)
//        json.putOpt("date", data)
//        val req = RequestInfo.newPost(deleteBillRecordUrl(), mapOf("Content-Type" to "application/json"), json.toString().toByteArray())
//        KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
//            Timber.tag("Mgr").e(it.dataToString())
//        }
//    }
}