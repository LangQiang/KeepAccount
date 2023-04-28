package com.godq.cms.procure.detail

import android.view.View
import com.godq.cms.R
import com.godq.cms.getEquipmentListUrl
import com.godq.cms.procure.ProcureEntity
import com.godq.cms.utils.parseEquipmentList
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/4/25 4:29 下午
 */
class EquipmentDetailVm {

    companion object {
        const val REQ_TYPE_INIT = 0
        const val REQ_TYPE_ADD = 1
        const val REQ_TYPE_UPDATE = 2
    }

    var procureEntity: ProcureEntity? = null

    var onDateCallback: ((type: Int, data: List<EquipmentEntity>?) -> Unit)? = null

    fun requestEquipmentList() {
        KwHttpMgr.getInstance().kwHttpFetch.asyncGet(RequestInfo.newGet(getEquipmentListUrl(procureEntity?.id))) {
            val data = it.dataToString()
            Timber.tag("equipment").e(data)
            onDateCallback?.invoke(REQ_TYPE_INIT, parseEquipmentList(data))
        }
    }

    fun create() {
        val temp = procureEntity?: return
        val fragment = NewEquipmentFragment(temp, null)
        fragment.createCallBack = {
            requestEquipmentList()
        }
        FragmentOperation.getInstance().showFullFragment(fragment)
    }

    fun onClickItem(equipmentEntity: EquipmentEntity) {

    }

    fun onClickChildItem(view: View?, equipmentEntity: EquipmentEntity) {
        when (view?.id) {
            R.id.menu_view -> {
                onMenuClick(equipmentEntity)
            }
            else -> {}
        }

    }

    private fun onMenuClick(equipmentEntity: EquipmentEntity) {
        procureEntity?.also {
            val fragment = NewEquipmentFragment(it, equipmentEntity)
            fragment.createCallBack = {
                requestEquipmentList()
            }
            FragmentOperation.getInstance().showFullFragment(fragment)
        }
    }
}