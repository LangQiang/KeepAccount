package com.godq.cms.asset.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.godq.cms.common.SimpleBaseFragment


/**
 * @author  GodQ
 * @date  2023/5/18 4:27 下午
 */
class AddAssetFragment: SimpleBaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent { AddAssetScreen() }
        }
    }
}