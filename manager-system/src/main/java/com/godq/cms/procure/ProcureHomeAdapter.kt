package com.godq.cms.procure

import com.chad.library.adapter.base.BaseQuickAdapter
import com.godq.cms.R
import com.godq.cms.databinding.ProcureHomeListItemLayoutBinding
import com.lazylite.mod.utils.DataBindBaseViewHolder


/**
 * @author  GodQ
 * @date  2023/4/25 4:16 下午
 */
class ProcureHomeAdapter(data: List<ProcureEntity>?)
    : BaseQuickAdapter<ProcureEntity, DataBindBaseViewHolder>(R.layout.procure_home_list_item_layout, data) {

    override fun convert(helper: DataBindBaseViewHolder?, item: ProcureEntity?) {
        (helper?.viewDataBinding as? ProcureHomeListItemLayoutBinding)?.takeIf {
            item != null
        }?.apply {
            this.itemData = item
            executePendingBindings()
        }
    }

}