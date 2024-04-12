package com.product.product.ui.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lazylite.mod.widget.BaseFragment
import com.product.product.databinding.ProductUploadFragmentBinding


/**
 * @author  GodQ
 * @date  2023/12/8 14:29
 */
class ProductUploadFragment: BaseFragment() {

    val vm = ProductUploadVM()

    private var binding: ProductUploadFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ProductUploadFragmentBinding.inflate(LayoutInflater.from(context))
        binding?.vm = vm
        return binding?.root
    }
}