package com.godq.cms.procure.detail

import com.chad.library.adapter.base.BaseQuickAdapter
import com.godq.cms.R
import com.godq.cms.databinding.EquipmentDetailListItemLayoutBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder


/**
 * @author  GodQ
 * @date  2023/4/25 4:16 下午
 */
class EquipmentDetailAdapter(data: List<EquipmentEntity>?)
    : BaseQuickAdapter<EquipmentEntity, DataBindBaseViewHolder>(R.layout.equipment_detail_list_item_layout, data) {

    override fun convert(helper: DataBindBaseViewHolder?, item: EquipmentEntity?) {
        (helper?.viewDataBinding as? EquipmentDetailListItemLayoutBinding)?.takeIf {
            item != null
        }?.apply {
            this.itemData = item
            helper.addOnClickListener(R.id.menu_view)
            executePendingBindings()
        }
    }

}