package com.product.product.ui.upload

import com.lazylite.mod.utils.toast.KwToast
import com.product.product.repo.ProductRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * @author  GodQ
 * @date  2023/12/8 14:38
 */
class ProductUploadVM {

    val uiState = ProductUploadUIState()

    private val repo = ProductRepo()

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun uploadMaterial() {
        scope.launch {
            if (repo.uploadMaterial(uiState.materialEtText))
                KwToast.show("上传成功")
            else
                KwToast.show("上传失败")
        }
    }

}