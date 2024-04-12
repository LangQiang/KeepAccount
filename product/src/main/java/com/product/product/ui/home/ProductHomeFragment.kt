package com.product.product.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lazylite.mod.widget.BaseFragment
import com.lazylite.mod.widget.indicator.base.wrapper.ViewPage2Wrapper
import com.lazylite.mod.widget.indicator.ui.home.HomeContainer
import com.product.product.databinding.ProductHomeFragmentBinding
import com.product.product.ui.material.ProductMaterialFragment


/**
 * @author  GodQ
 * @date  2023/12/3 16:08
 */
class ProductHomeFragment: BaseFragment() {

    private var binding: ProductHomeFragmentBinding? = null

    private val vm = ProductHomeVM()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ProductHomeFragmentBinding.inflate(LayoutInflater.from(context))
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tBinding = binding?: return
        addViewPaddingTop(tBinding.statusBarHolder)

        tBinding.vp2.adapter = ProductVp2Adapter(this, listOf(ProductMaterialFragment(), ProductMaterialFragment()))

        tBinding.indicator.container = ProductIndicatorContainer(view.context).apply {
            setTitles(listOf("商品", "原材料"))
        }
        tBinding.indicator.bind(ViewPage2Wrapper(tBinding.vp2))

    }

    class ProductVp2Adapter(parent: Fragment, private val fragments: List<Fragment>) : FragmentStateAdapter(parent) {
        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

    }

}