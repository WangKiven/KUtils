package com.kiven.pushlibrary

import android.content.Context

/**
 * Created by oukobayashi on 2020-01-13.
 */
internal interface PushHelper {
    var hasInitSuccess:Boolean
    fun initPush(context: Context, isAgreePrivacy: Boolean)
    fun setTags(context: Context, tags:Set<String>)
//    fun clearTags(context: Context)
//    fun setAccount(context: Context, account:String)
//    fun removeAccount(context: Context)
}