package com.godq.cms

import androidx.fragment.app.Fragment
import com.godq.cms.procure.ProcureHomeFragment
import com.godq.msa.IManagerService


/**
 * @author  GodQ
 * @date  2023/4/25 3:36 下午
 */
class ManagerServiceImpl: IManagerService {
    override fun getMgrHomeFragment(): Fragment {
        return BillMgrHomeFragment()
    }

    override fun getProcureHomeFragment(): Fragment {
        return ProcureHomeFragment()
    }
}