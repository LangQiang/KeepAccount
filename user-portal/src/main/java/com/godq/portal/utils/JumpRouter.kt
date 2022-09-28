package com.godq.portal.utils

import com.godq.portal.billdetail.BillDetailFragment
import com.lazylite.mod.fragmentmgr.FragmentOperation

fun jumpToBillDetail(shopId: String) {
    FragmentOperation.getInstance().showFullFragment(BillDetailFragment.getInstance(shopId))
}
