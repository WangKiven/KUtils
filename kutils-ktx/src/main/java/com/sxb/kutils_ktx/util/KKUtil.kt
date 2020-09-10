package com.sxb.kutils_ktx.util

import android.os.Parcel
import android.os.Parcelable
import kotlinx.coroutines.*

/**
 * 根据 Parcel 生成 Parcelable 对象的实例。由于 Kotlin 的问题，导致 @Parcelize注解的Parcelable子类 不能直接调用 CREATOR ，只能通过反射来了。
 */
fun <T : Parcelable> createFromParcel(t: Class<T>, parcel: Parcel): T {
    val field = t.getField("CREATOR")
    val creator = field.get(t) as Parcelable.Creator<T>
    return creator.createFromParcel(parcel)
}

/**
 * GlobalScope主线程调用
 */
fun GlobalScope.main(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch(Dispatchers.Main, block = block)