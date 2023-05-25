package com.godq.cms.asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.godq.cms.common.SimpleBaseFragment


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