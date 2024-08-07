package com.kiven.sample.media

import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.util.Const.IMAGE_DIR
import java.io.File

/**
 * Created by wangk on 2019/5/17.
 *
 *  TODO: 2019/5/17  https://developer.android.google.cn/training/camerax
 *  手把手：https://codelabs.developers.google.com/codelabs/camerax-getting-started#0
 */
class AHCameraxTest : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)



        /*val imageCaptureConfig = ImageCaptureConfig.Builder()
                .setTargetRotation(mActivity.windowManager.defaultDisplay.rotation)
                .build()
        val imageCapture = ImageCapture(imageCaptureConfig)


        val previewConfig = PreviewConfig.Builder()
                .build()
        val preview = Preview(previewConfig)*/


        setContentView(LinearLayout(activity).apply {

            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL


            /*textureView {
                layoutParams = ViewGroup.LayoutParams(dip(200), dip(300))

                preview.setOnPreviewOutputUpdateListener { previewOutput ->
                    surfaceTexture = previewOutput.surfaceTexture
                }
                CameraX.bindToLifecycle(mActivity as LifecycleOwner, imageCapture, preview)
            }*/


            addView(Button(activity).apply {
                text = "拍照"

                setOnClickListener {
                    val file = getFile(System.currentTimeMillis().toString() + ".jpg")
                    /*imageCapture.takePicture(file, Executor {  }, object :ImageCapture.OnImageSavedListener{
                        override fun onImageSaved(file: File) {
                            KUtil.addPicture(file.absolutePath) { _, _ ->
                            }
                        }

                        override fun onError(imageCaptureError: ImageCapture.ImageCaptureError, message: String, cause: Throwable?) {
                        }
                    })*/
                }
            })
        })
    }

    private fun getFile(fileName: String): File {
        KUtil.getAppFileFolderPath()
        val dir = File(Environment.getExternalStorageDirectory(), IMAGE_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }
}