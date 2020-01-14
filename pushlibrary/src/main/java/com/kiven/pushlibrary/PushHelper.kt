package com.kiven.pushlibrary

import android.content.Context

/**
 * Created by oukobayashi on 2020-01-13.
 */
interface PushHelper {
    fun initPush(context: Context)
    fun setTags(context: Context, tags:Set<String>)
    fun clearTags(context: Context)
    fun setAccount(context: Context, account:String)
    fun removeAccount(context: Context)
}