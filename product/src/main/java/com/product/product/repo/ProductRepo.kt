package com.product.product.repo

import com.lazylite.mod.http.mgr.KwHttpMgr
import com.lazylite.mod.http.mgr.model.RequestInfo
import com.product.product.entity.ProductMaterialEntity
import com.product.product.getMaterialListUrl
import com.product.product.getUploadMaterialUrl
import com.product.product.ui.upload.ProductUploadParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber


/**
 * @author  GodQ
 * @date  2023/12/8 14:51
 */
class ProductRepo {

    private val parser = ProductUploadParser()


    suspend fun getMaterialList(): List<ProductMaterialEntity> {
        return withContext(Dispatchers.IO) {
            val jsonStr = KwHttpMgr.getInstance().kwHttpFetch.get(RequestInfo.newGet(getMaterialListUrl())).dataToString()
            Timber.tag("ProductRepo").e(jsonStr)
            parser.parseMaterialList(jsonStr)
        }
    }

    suspend fun uploadMaterial(materialEtText: String): Boolean {
        val entity = parser.parseUploadMaterialInfo(materialEtText) ?: return false
        return withContext(Dispatchers.IO) {
            val header = mapOf("Content-Type" to "application/json")
            val json = JSONObject()
            json.putOpt("material_name", entity.name)
            json.putOpt("material_price_per", entity.pricePer)
            json.putOpt("material_quantity_unit", entity.quantityUnit)
            json.putOpt("material_channel", entity.channel)
            json.putOpt("material_channel_contact_info", entity.channelContactInfo)
            json.putOpt("material_type", entity.type)
            json.putOpt("material_belong", entity.belong)
            json.putOpt("material_notes", entity.notes)
            Timber.tag("ProductRepo").e(json.toString())
            val reqInfo = RequestInfo.newPost(getUploadMaterialUrl(), header, json.toString().toByteArray())
            KwHttpMgr.getInstance().kwHttpFetch.post(reqInfo).isSuccessful
        }
    }
}