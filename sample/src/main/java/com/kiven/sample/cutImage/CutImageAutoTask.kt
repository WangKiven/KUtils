package com.kiven.sample.cutImage

import android.view.accessibility.AccessibilityEvent
import com.kiven.sample.autoService.AutoInstallService
import com.kiven.sample.autoService.AutoTaskInterface

class CutImageAutoTask() : AutoTaskInterface {
    override var isClose: Boolean = false

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

    }

    override fun registerService(service: AutoInstallService) {

    }

    override fun close() {

    }

    override fun pause() {

    }
}