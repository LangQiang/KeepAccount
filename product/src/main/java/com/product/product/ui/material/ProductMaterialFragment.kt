package com.product.product.ui.material

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lazylite.mod.widget.BaseFragment
import com.product.product.R


/**
 * @author  GodQ
 * @date  2023/12/3 16:04
 */
class ProductMaterialFragment: BaseFragment() {

    val vm = ProductMaterialVM()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(vm)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.product_material_fragment, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.onMaterialListChangedListener = {

        }
        vm.getMaterialList()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(vm)
    }

}