package com.sxb.kutils_ktx.util

import com.kiven.kutils.logHelper.KLog

/**
 * Created by oukobayashi on 2019-12-27.
 */

fun printClassField(obj:Any?, isDeclared:Boolean = true) {
    if (obj is Class<*>) KLog.printClassField(null, obj, isDeclared)
    else KLog.printClassField(obj, null, isDeclared)
}