package com.godq.cms.procure.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.godq.sideslidemenuwidget.SideSlideMenuLayout
import cn.godq.sideslidemenuwidget.SideSlideRVItemTouchListener
import com.godq.cms.R
import com.godq.cms.databinding.EquipmentDetailFragmentLayoutBinding
import com.godq.cms.procure.ProcureEntity
import com.godq.cms.procure.ProcureHomeVm
import com.lazylite.mod.widget.BaseFragment


/**
 * @author  GodQ
 * @date  2023/4/25 6:14 下午
 */
class EquipmentDetailFragment: BaseFragment() {

    private var procureEntity: ProcureEntity? = null

    private var binding: EquipmentDetailFragmentLayoutBinding? = null

    var vm = EquipmentDetailVm()

    private var mAdapter = EquipmentDetailAdapter(null)

    companion object {
        fun getInstance(procureEntity: ProcureEntity): Fragment {
            val fragment = EquipmentDetailFragment()
            fragment.procureEntity = procureEntity
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm.procureEntity = procureEntity
        binding = EquipmentDetailFragmentLayoutBinding.inflate(LayoutInflater.from(context))
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.titleBar?.apply {
            setMainTitle("清单列表")
            setRightTextBtn("新增")
            setRightListener {
                vm.create()
            }
        }
        binding?.equipmentRv?.apply {
            layoutManager = LinearLayoutManager(context)
            mAdapter.bindToRecyclerView(this)
            this.addOnItemTouchListener(SideSlideRVItemTouchListener())
        }

        mAdapter.setOnItemClickListener { adapter, v, position ->
            (adapter.getViewByPosition(position, R.id.menu_layout) as? SideSlideMenuLayout)?.close()
//            (adapter.data[position] as? EquipmentEntity)?.apply {
//                vm.onClickItem(this)
//            }
        }
        mAdapter.setOnItemChildClickListener { adapter, v, position ->
            (adapter.getViewByPosition(position, R.id.menu_layout) as? SideSlideMenuLayout)?.close()
            (adapter.data[position] as? EquipmentEntity)?.apply {
                vm.onClickChildItem(v, this)
            }
        }

        vm.onDateCallback = { type, data ->
            if (type == ProcureHomeVm.REQ_TYPE_INIT) {
                mAdapter.setNewData(data)
            } else {

            }
        }

        vm.requestEquipmentList()
    }


}