package com.kiven.sample.cutImage

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.autoService.AutoInstallService
import com.kiven.sample.floatView.ServiceFloat
import com.kiven.sample.util.showImageDialog
import com.kiven.sample.util.showToast
import com.kiven.sample.util.startOverlaySetting

class AHCutImage: BaseFlexActivityHelper() {
    private val mediaProjectionManager by lazy { mActivity.getSystemService(Service.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var media: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private val bitmaps = mutableListOf<Bitmap>()

    private val width by lazy { KUtil.getScreenWith() }
    private val height by lazy { KUtil.getScreenHeight() }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        launcher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data == null) return@registerForActivityResult

            try {
                media = mediaProjectionManager.getMediaProjection(it.resultCode, it.data!!)
                imageReader = ImageReader.newInstance(width, height, ImageFormat.RGB_565, 10).apply {
                    val vd = media!!.createVirtualDisplay("mediaProjection", width, height, KUtil.getScreenDensityDpi(),
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, surface, null, null)
                }
            } catch (e: Throwable) {
                KLog.e(e)
            }
        }

        addTitle("MediaProjectionManager，必须开启前台服务功能并通知，否则高系统会报异常")

        addBtn("开启截图前台服务") {
            CutScreenService.putUI("截", {
                if (imageReader == null) return@putUI

                val image = imageReader!!.acquireLatestImage() ?: return@putUI
                val planes = image.planes
                if (planes.isEmpty()) return@putUI

                val plane = planes[0]
                val buffer = plane.buffer
                val ps = plane.pixelStride
                val rs = plane.rowStride
                val rp = rs - ps * width

                val bitmap = Bitmap.createBitmap(width + rp/ps, height, Bitmap.Config.RGB_565)
                bitmap.copyPixelsFromBuffer(buffer)
                image.close()

                bitmaps.add(Bitmap.createBitmap(bitmap))
            }) {
                val channelId = "mediaProjection_xx"
                val notificationId = 343

                val notification = NotificationCompat.Builder(it, channelId)
                    .setContentTitle("hhhhhh")
                    .build()
                val channel = NotificationChannelCompat.Builder(channelId,3)
                    .setName("录屏通知")
                    .build()
                val notiManager = NotificationManagerCompat.from(it)
                notiManager.createNotificationChannel(channel)
                it.startForeground(notificationId, notification)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(mActivity)) {
                    activity.startOverlaySetting()
                    return@addBtn
                }
            }

            startAppOutFloat()
        }

        addBtn("请求屏幕截图权限") {
            if (imageReader == null) {
                launcher.launch(mediaProjectionManager.createScreenCaptureIntent())
            } else {
                showToast("已获得权限")
            }
        }

        addBtn("显示图片") {
            bitmaps.lastOrNull()?.apply {
                activity.showImageDialog(this)
            }
        }

        addTitle("AccessibilityService")
        addBtn("开始") {
            AutoInstallService.startWXTask(activity, CutImageAutoTask())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        media?.stop()
        imageReader = null
        stopAppOutFloat()
    }

    private fun startAppOutFloat() {
        val intent = Intent(mActivity, CutScreenService::class.java)
        if (!KUtil.isRun(CutScreenService::class.java)) {
            mActivity.startService(intent)
        }
    }

    private fun stopAppOutFloat() {
        val intent = Intent(mActivity, CutScreenService::class.java)
        if (KUtil.isRun(CutScreenService::class.java)) {
            mActivity.stopService(intent)
        }
    }
}