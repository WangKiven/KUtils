package com.kiven.sample.media

import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KUtil
import org.jetbrains.anko.*
import java.io.File

/**
 * Created by wangk on 2019/5/17.
 *
 *  TODO: 2019/5/17  https://developer.android.google.cn/training/camerax
 */
class AHCameraxTest : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)



        val imageCaptureConfig = ImageCaptureConfig.Builder()
                .setTargetRotation(mActivity.windowManager.defaultDisplay.rotation)
                .build()
        val imageCapture = ImageCapture(imageCaptureConfig)


        val previewConfig = PreviewConfig.Builder()
                .build()
        val preview = Preview(previewConfig)


        mActivity.linearLayout {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL


            textureView {
                layoutParams = ViewGroup.LayoutParams(dip(200), dip(300))

                preview.setOnPreviewOutputUpdateListener { previewOutput ->
                    surfaceTexture = previewOutput.surfaceTexture
                }
                CameraX.bindToLifecycle(mActivity as LifecycleOwner, imageCapture, preview)
            }

            button {
                text = "拍照"

                setOnClickListener {
                    val file = getFile(System.currentTimeMillis().toString() + ".jpg")
                    imageCapture.takePicture(file, object : ImageCapture.OnImageSavedListener {
                        override fun onImageSaved(file: File) {
                            KUtil.addPicture(file.absolutePath) { _, _ ->
                            }
                        }

                        override fun onError(useCaseError: ImageCapture.UseCaseError, message: String, cause: Throwable?) {
                        }
                    })
                }
            }
        }
    }

    private val IMAGE_DIR = "KUtilSampleFile" + File.separator + "testImage"
    private fun getFile(fileName: String): File {
        val dir = File(Environment.getExternalStorageDirectory(), IMAGE_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }
}