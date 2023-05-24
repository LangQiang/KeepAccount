package com.godq.cms.asset

import com.google.gson.annotations.SerializedName


/**
 * @author  GodQ
 * @date  2023/5/16 3:46 下午
 */
data class AssetData(

    @SerializedName("asset_id")
    val id: String = "",
    @SerializedName("asset_title")
    val title: String = "",
    @SerializedName("asset_belong")
    val belong: String = "",
    @SerializedName("asset_format")
    val format: String = "",
    @SerializedName("asset_url")
    val url: String = "",
    @SerializedName("asset_extra")
    val extra: String = "",
    @SerializedName("asset_created_time")
    val createdTime: String = "",
) {
    companion object {
        const val FORMAT_PDF = "PDF"
        const val FORMAT_PIC = "PIC"
        const val FORMAT_TEXT = "TEXT"
        const val FORMAT_WORD = "WORD"
    }
}