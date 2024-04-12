package com.product.product.entity


/**
 * @author  GodQ
 * @date  2023/12/8 14:54
 */
data class ProductMaterialEntity(
    val id: String = "",
    val name: String = "",
    val pricePer: Double = 0.0,
    val quantityUnit: String = "",
    val channel: String = "",
    val channelContactInfo: String = "",
    val type: String = "",
    val belong: String = "",
    val notes: String = "",
)