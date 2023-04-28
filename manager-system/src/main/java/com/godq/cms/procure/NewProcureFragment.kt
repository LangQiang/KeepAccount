package com.godq.cms.procure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import com.godq.cms.databinding.NewProcureFragmentLayoutBinding
import com.godq.cms.getCreateProcureUrl
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.utils.toast.KwToast
import com.lazylite.mod.widget.BaseFragment
import org.json.JSONObject
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/4/25 4:48 下午
 */
class NewProcureFragment: BaseFragment() {

    var createCallBack: ((success: Boolean) -> Unit)? = null

    var binding: NewProcureFragmentLayoutBinding? = null

    var title = ObservableField("")
    var desc = ObservableField("")
    var notes = ObservableField("")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = NewProcureFragmentLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.fragment = this
        return binding?.root
    }

    fun onCommitClick() {
        val title = title.get()
        val desc = desc.get()
        val notes = notes.get()
        if (title.isNullOrEmpty()) {
            KwToast.show("不可以创建空标题的清单")
            return
        }
        val json = JSONObject()
        json.putOpt("procure_name", title)
        json.putOpt("procure_desc", desc)
        json.putOpt("procure_notes", notes)
        val req = RequestInfo.newPost(getCreateProcureUrl(), mapOf("Content-Type" to "application/json"), json.toString().toByteArray())
        KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
            Timber.tag("procure").e(it.dataToString())
            createCallBack?.invoke(true)
            close()
        }
    }
}