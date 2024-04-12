package com.product.product

import android.content.Context
import android.util.Pair
import com.lazylite.annotationlib.AutoInit
import com.lazylite.bridge.init.Init
import com.lq.product_api.IProductService


/**
 * @author  GodQ
 * @date  2023/12/3 15:59
 */
@AutoInit
class ProductInit: Init() {
    override fun init(context: Context?) {
    }

    override fun initAfterAgreeProtocol(context: Context?) {
    }

    override fun getServicePair(): Pair<String, Any> {
        return Pair(IProductService::class.java.name, ProductServiceImpl())
    }
}