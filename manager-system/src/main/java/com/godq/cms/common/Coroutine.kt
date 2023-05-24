package com.godq.cms.common

import com.lazylite.mod.utils.toast.KwToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler


/**
 * @author  GodQ
 * @date  2023/5/19 11:20 上午
 */
val globalCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    if (throwable is CancellationException) {
        // 处理协程被取消的情况
        KwToast.show("cancel")
    } else {
        // 处理其他异常
        KwToast.show("error: ${throwable.message}")
    }
}