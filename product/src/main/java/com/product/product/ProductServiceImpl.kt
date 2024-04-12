package com.product.product

import androidx.fragment.app.Fragment
import com.lq.product_api.IProductService
import com.product.product.ui.home.ProductHomeFragment


/**
 * @author  GodQ
 * @date  2023/12/3 16:01
 */
class ProductServiceImpl: IProductService {
    override fun getProductHomeFragment(): Fragment {
        return ProductHomeFragment()
    }
}