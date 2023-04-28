package com.godq.cms.procure

import com.godq.cms.getProcureListUrl
import com.godq.cms.procure.detail.EquipmentDetailFragment
import com.godq.cms.utils.parseProcureList
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.lazylite.mod.utils.toast.KwToast
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/4/25 4:29 下午
 */
class ProcureHomeVm {

    companion object {
        const val REQ_TYPE_INIT = 0
        const val REQ_TYPE_ADD = 1
        const val REQ_TYPE_UPDATE = 2
    }

    var onDateCallback: ((type: Int, data: List<ProcureEntity>?) -> Unit)? = null

    fun requestProcureList() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet(getProcureListUrl())) {
            val data = it.dataToString()
            Timber.tag("procure").e(data)
            onDateCallback?.invoke(REQ_TYPE_INIT, parseProcureList(data))
        }
    }

    fun create() {
        val fragment = NewProcureFragment()
        fragment.createCallBack = {
            requestProcureList()
        }
        FragmentOperation.getInstance().showFullFragment(fragment)
    }

    fun onClickItem(procureEntity: ProcureEntity) {
        FragmentOperation.getInstance().showFullFragment(EquipmentDetailFragment.getInstance(procureEntity))
    }
}