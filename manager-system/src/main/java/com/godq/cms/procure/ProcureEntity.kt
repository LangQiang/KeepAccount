package com.godq.cms.procure

data class ProcureEntity(
    val id: String?,
    val name: String,
    val desc: String,
    val notes: String,
    val state: Int,
    val createTime: String,
    )
