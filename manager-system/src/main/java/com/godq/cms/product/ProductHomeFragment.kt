package com.godq.cms.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.godq.cms.common.SimpleBaseFragment
import com.godq.cms.databinding.CastListFragmentBinding


/**
 * @author  GodQ
 * @date  2023/11/3 10:17
 */
class ProductHomeFragment: SimpleBaseFragment() {

    private var binding: CastListFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CastListFragmentBinding.inflate(LayoutInflater.from(context))
        return binding?.root
    }


}