package com.godq.cms.asset

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.godq.cms.common.SimpleBaseFragment
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.fragmentmgr.IFragment


/**
 * @author  GodQ
 * @date  2023/5/15 6:12 下午
 */
class AssetListFragment: SimpleBaseFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AssetFragmentScreen()
            }
        }
    }
}