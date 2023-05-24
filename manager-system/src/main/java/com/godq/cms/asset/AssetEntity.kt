package com.godq.cms.asset

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


/**
 * @author  GodQ
 * @date  2023/5/23 11:14 上午
 */
data class AssetEntity(val assetData: AssetData, val downloadState: MutableState<String> = mutableStateOf("下载"))