package com.godq.portal.ext


/**
 * @author  GodQ
 * @date  2023/9/19 17:37
 */

fun Float.scale(digits: Int): Float {
    return "%.${digits}f".format(this).toFloat()
}

fun String.safeToDouble(): Double {
    return try {
        this.toDouble()
    } catch (e: Exception) {
        0.0
    }
}