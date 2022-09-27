package com.godq.keepaccounts.utils

import com.godq.keepaccounts.billdetail.BillDetailFragment
import com.godq.keepaccounts.mgrbg.update.BillUpdateFragment
import com.lazylite.mod.fragmentmgr.FragmentOperation

fun jumpToBillDetail(shopId: String) {
    FragmentOperation.getInstance().showFullFragment(BillDetailFragment.getInstance(shopId))
}

fun jumpToBillUpdateFragment() {
    FragmentOperation.getInstance().showFullFragment(BillUpdateFragment())
}