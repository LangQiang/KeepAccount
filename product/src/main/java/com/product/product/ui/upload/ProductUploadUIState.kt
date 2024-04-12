package com.product.product.ui.upload

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.product.product.BR


/**
 * @author  GodQ
 * @date  2023/12/8 14:42
 */
class ProductUploadUIState: BaseObservable() {
    @get:Bindable
    var materialEtText = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.materialEtText)
        }

}