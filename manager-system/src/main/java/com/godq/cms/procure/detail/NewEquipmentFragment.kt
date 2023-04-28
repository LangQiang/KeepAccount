package com.godq.cms.procure.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import com.godq.cms.ManagerLinkHelper
import com.godq.cms.R
import com.godq.cms.databinding.NewEquipmentFragmentLayoutBinding
import com.godq.cms.getCreateEquipmentUrl
import com.godq.cms.getUpdateEquipmentUrl
import com.godq.cms.procure.ProcureEntity
import com.godq.ulda.IUploadService
import com.lazylite.mod.App
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
class NewEquipmentFragment(val procureEntity: ProcureEntity, private val equipmentEntity: EquipmentEntity?): BaseFragment() {

    var createCallBack: ((success: Boolean) -> Unit)? = null

    var binding: NewEquipmentFragmentLayoutBinding? = null

    var name = ObservableField("")
    var pic = ObservableField("")
    var desc = ObservableField("")
    var notes = ObservableField("")
    var state = ObservableField(0)
    var count = ObservableField(1)
    var price = ObservableField(0)
    var buyChannel = ObservableField("")
    var completeDate = ObservableField("")
    var purchaser = ObservableField("")

    var isCreateOpt = ObservableField(true)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = NewEquipmentFragmentLayoutBinding.inflate(LayoutInflater.from(context))
        binding?.fragment = this
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        binding?.stateRadioGroup?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.todo -> {
                    state.set(0)
                }
                R.id.discard -> {
                    state.set(1)
                }
                R.id.complete -> {
                    state.set(2)
                }
                else -> {}
            }
        }
    }

    private fun setupView() {
        isCreateOpt.set(equipmentEntity == null)

        equipmentEntity?.apply {
            name.set(this.equipment_name)
            pic.set(this.equipment_pic)
            desc.set(this.equipment_desc)
            notes.set(this.equipment_notes)
            when (this.equipment_state) {
                EquipmentEntity.STATE_TODO -> {
                    binding?.stateRadioGroup?.check(R.id.todo)
                }
                EquipmentEntity.STATE_DISCARD -> {
                    binding?.stateRadioGroup?.check(R.id.discard)
                }
                EquipmentEntity.STATE_COMPLETE -> {
                    binding?.stateRadioGroup?.check(R.id.complete)
                }
            }
            count.set(this.equipment_count)
            price.set(this.equipment_per_price)
            buyChannel.set(this.equipment_buy_channel)
            completeDate.set(this.equipment_complete_date)
            purchaser.set(this.equipment_purchaser)
        }
    }

    fun onSelectImageClick() {
        val activity = App.getMainActivity()?: return
        ManagerLinkHelper.chooseImage(activity, object : IUploadService.OnChooseImageCallback {
            override fun onChoose(fileUri: String?) {
                uploadFileByPathToCos(fileUri)
            }
        })
    }

    private fun uploadFileByPathToCos(filePath: String?) {
        if (filePath.isNullOrEmpty()) {
            KwToast.show("上传失败，无法获取所选文件路径")
            return
        }
        ManagerLinkHelper.upload(filePath, object : IUploadService.OnUploadCallback {
            override fun onUpload(accessUrl: String?) {
                if (accessUrl.isNullOrEmpty()) {
                    KwToast.show("无法上传文件到服务器")
                    return
                }
                pic.set(accessUrl)
            }
        })
    }

    fun onCommitClick() {
        val procureId = procureEntity.id
        val name = name.get()
        val pic = pic.get()
        val desc = desc.get()
        val notes = notes.get()
        val state = state.get()
        val count = count.get()
        val price = price.get()
        val buyChannel = buyChannel.get()
        val completeDate = completeDate.get()
        val purchaser = purchaser.get()
        if (name.isNullOrEmpty()) {
            KwToast.show("不可以创建空标题的清单")
            return
        }

        val json = JSONObject()

        if (isCreateOpt.get() == true) {
            json.putOpt("procure_id", procureId)
            json.putOpt("equipment_name", name)
            json.putOpt("equipment_pic", pic)
            json.putOpt("equipment_desc", desc)
            json.putOpt("equipment_count", count)
            json.putOpt("equipment_per_price", price)
            json.putOpt("equipment_buy_channel", buyChannel)
        } else {
            json.putOpt("equipment_id", equipmentEntity?.equipmentId)
        }

        json.putOpt("equipment_notes", notes)
        json.putOpt("equipment_state", state)
        json.putOpt("equipment_complete_date", completeDate)
        json.putOpt("equipment_purchaser", purchaser)
        val req = RequestInfo.newPost(if (isCreateOpt.get() == true) getCreateEquipmentUrl() else getUpdateEquipmentUrl(), mapOf("Content-Type" to "application/json"), json.toString().toByteArray())
        KwHttpMgr.getInstance().kwHttpFetch.asyncPost(req) {
            Timber.tag("equipment").e(it.dataToString())
            createCallBack?.invoke(true)
            close()
        }
    }
}