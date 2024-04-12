package com.product.product

import android.net.Uri
import com.godq.deeplink.route.AbsRouter
import com.lazylite.annotationlib.DeepLink
import com.lazylite.mod.fragmentmgr.FragmentOperation
import com.lazylite.mod.utils.toast.KwToast
import com.product.product.ui.upload.ProductUploadFragment


/**
 * @author  GodQ
 * @date  2023/12/8 13:08
 */
@DeepLink(path = "/cms/productUpload")
class ProductRouter: AbsRouter() {
    override fun parse(p0: Uri?) {

    }

    override fun route(): Boolean {
        FragmentOperation.getInstance().showFullFragment(ProductUploadFragment())
        return true
    }
}