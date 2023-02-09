package com.kiven.sample.autoService

import android.view.accessibility.AccessibilityEvent

interface AutoTaskInterface {
    fun onAccessibilityEvent(event: AccessibilityEvent)

    fun registerService(service: AutoInstallService)

    fun close()

    fun pause() // 暂停

    var isClose: Boolean
}