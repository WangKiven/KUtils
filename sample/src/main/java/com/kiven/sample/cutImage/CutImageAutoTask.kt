package com.kiven.sample.cutImage

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Build
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import com.kiven.kutils.tools.KFile
import com.kiven.sample.autoService.AutoInstallService
import com.kiven.sample.autoService.AutoTaskInterface
import com.kiven.sample.util.showToast

class CutImageAutoTask : AutoTaskInterface {
    override var isClose: Boolean = false

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

    }

    override fun registerService(service: AutoInstallService) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            showToast("5秒后开始截图，请快速切换到需截图的界面~")
            Thread.sleep(5000)

            service.takeScreenshot(Display.DEFAULT_DISPLAY, service.mainExecutor, object :
                AccessibilityService.TakeScreenshotCallback {
                override fun onSuccess(p0: AccessibilityService.ScreenshotResult) {
                    val bitmap = Bitmap.wrapHardwareBuffer(p0.hardwareBuffer, p0.colorSpace) ?: return
                    KFile.saveJpgBitmap(service, bitmap, "", "")
                }

                override fun onFailure(p0: Int) {
                }
            })
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            showToast("5秒后开始截图，请快速切换到需截图的界面")
            Thread.sleep(5000)
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
            Thread.sleep(500)
        } else {
            showToast("系统版本低，不能截图")
        }

        /*runBlocking {
            async {

            }
        }*/
    }

    override fun close() {

    }

    override fun pause() {

    }
}