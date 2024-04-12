package com.product.product.ui.upload

import com.product.product.entity.ProductMaterialEntity
import org.json.JSONObject
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/12/8 15:13
 */
class ProductUploadParser {

    fun parseMaterialList(jsonStr: String?): List<ProductMaterialEntity> {
        return listOf()
    }

    fun parseUploadMaterialInfo(jsonStr: String): ProductMaterialEntity? {
        return try {
            val jsonObj = JSONObject(jsonStr)
            ProductMaterialEntity(
                name = jsonObj.optString("material_name"),
                pricePer = jsonObj.optDouble("material_price_per", 0.0),
                quantityUnit = jsonObj.optString("material_quantity_unit"),
                channel = jsonObj.optString("material_channel"),
                channelContactInfo = jsonObj.optString("material_channel_contact_info"),
                type = jsonObj.optString("material_type"),
                belong = jsonObj.optString("material_belong"),
                notes = jsonObj.optString("material_notes"),
            )
        } catch (e: Exception) {
            null
        }
    }
}