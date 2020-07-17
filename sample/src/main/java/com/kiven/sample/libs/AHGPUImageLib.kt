package com.kiven.sample.libs

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.file.KFile
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KImage
import com.kiven.kutils.tools.KUtil
import com.kiven.kutils.widget.RulingSeekbar
import com.kiven.sample.R
import com.kiven.sample.util.randomPhoneImage
import com.kiven.sample.util.showBottomSheetDialog
import com.kiven.sample.util.showImageDialog
import com.kiven.sample.util.showListDialog
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import kotlinx.android.synthetic.main.ah_gpu_image_lib.*
import kotlinx.android.synthetic.main.item_gpu_test_seekbar.view.*
import java.io.File
import java.io.FileInputStream
import java.lang.reflect.Modifier

/**
 * Created by oukobayashi on 2020/7/16.
 */
class AHGPUImageLib : KActivityDebugHelper() {
    private var imageUri: Uri? = null
        set(value) {
            field = value
            mActivity.imageView.setImage(value)
            mActivity.imageView_.setImageURI(value)
        }

    private var filterItem: FilterItem<*>? = null
        set(value) {
            val b = field != value
            field = value
            if (b) mActivity.imageView.filter = value?.filter
            value?.createLeftDrawer()
            if (value == null)
                mActivity.filterName.text = ""
            else {
                mActivity.filterName.text = "${filters.indexOf(value)} - ${value.name}"
            }
        }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_gpu_image_lib)
        activity.apply {
            imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
            filterItem = filters[0]
            randomPhoneImage { imageUri = it }

            button.setOnClickListener {
                showBottomSheetDialog(listOf(
                        "修改属性",
                        "随机图片",
                        "选择图片",
                        "选择filter",
                        "随机filter",
                        "下一个filter",
                        "保存"
                )) { p, _ ->
                    when (p) {
                        0 -> {
                            drawer.openDrawer(Gravity.LEFT)
                        }
                        1 -> {
                            randomPhoneImage { imageUri = it }
                        }
                        2 -> {
                            mActivity.startActivityForResult(
                                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 345)
                        }
                        3 -> {
                            var i = 0
                            showListDialog(filters.map { "${i++}" + it.name }, true) { p, _ ->
                                filterItem = filters[p]
                            }
                        }
                        4 -> {
                            filterItem = filters.random()
                        }
                        5 -> {
                            var i = if (filterItem == null) -1 else filters.indexOf(filterItem!!)
                            if (i >= filters.size - 1) {
                                i = -1
                            }

                            filterItem = filters[i + 1]
                        }
                        6 -> {
                            imageView.saveToPictures("gpuImages", "image-${System.currentTimeMillis()}") { uri ->
                                KLog.i(uri.toString())
                                showImageDialog(uri)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            345 -> {
                val uri = data?.data ?: return
                imageUri = uri
            }
        }
    }

    private val filters = listOf<FilterItem<*>>(
            FilterItem("GPUImageHueFilter", listOf(FloatMethod("setHue", 360f, 90f))) { GPUImageHueFilter() },
            FilterItem("GPUImageAddBlendFilter", listOf()) { GPUImageAddBlendFilter() },
            FilterItem("GPUImageAlphaBlendFilter", listOf()) { GPUImageAlphaBlendFilter() },
            FilterItem("GPUImageBilateralBlurFilter", listOf(FloatMethod("setDistanceNormalizationFactor", 10f, 8f))) { GPUImageBilateralBlurFilter() },
            FilterItem("GPUImageBoxBlurFilter", listOf()) { GPUImageBoxBlurFilter() },
            FilterItem("GPUImageBrightnessFilter", listOf()) { GPUImageBrightnessFilter() },
            FilterItem("GPUImageBulgeDistortionFilter-局部放大", listOf()) { GPUImageBulgeDistortionFilter() },
            FilterItem("GPUImageCGAColorspaceFilter-CGA模拟", listOf()) { GPUImageCGAColorspaceFilter() },
            FilterItem("GPUImageChromaKeyBlendFilter-2图片融合", listOf()) { GPUImageChromaKeyBlendFilter() },
            FilterItem("GPUImageColorBalanceFilter", listOf()) { GPUImageColorBalanceFilter() },
            FilterItem("GPUImageColorBlendFilter", listOf()) { GPUImageColorBlendFilter() },
            FilterItem("GPUImageColorBurnBlendFilter", listOf()) { GPUImageColorBurnBlendFilter() },
            FilterItem("GPUImageColorDodgeBlendFilter", listOf()) { GPUImageColorDodgeBlendFilter() },
            FilterItem("GPUImageColorInvertFilter", listOf()) { GPUImageColorInvertFilter() },
            FilterItem("GPUImageColorMatrixFilter", listOf()) { GPUImageColorMatrixFilter() },
            FilterItem("GPUImageContrastFilter", listOf()) { GPUImageContrastFilter() },
            FilterItem("GPUImageCrosshatchFilter", listOf()) { GPUImageCrosshatchFilter() },

            FilterItem("GPUImageDarkenBlendFilter", listOf()) { GPUImageDarkenBlendFilter() },
            FilterItem("GPUImageDifferenceBlendFilter", listOf()) { GPUImageDifferenceBlendFilter() },
            FilterItem("GPUImageDilationFilter", listOf()) { GPUImageDilationFilter() },
            FilterItem("GPUImageDirectionalSobelEdgeDetectionFilter", listOf()) { GPUImageDirectionalSobelEdgeDetectionFilter() },
            FilterItem("GPUImageDissolveBlendFilter", listOf()) { GPUImageDissolveBlendFilter() },
            FilterItem("GPUImageDivideBlendFilter", listOf()) { GPUImageDivideBlendFilter() },

            FilterItem("GPUImageAlphaBlendFilter", listOf()) { GPUImageEmbossFilter() },
            FilterItem("GPUImageExclusionBlendFilter", listOf()) { GPUImageExclusionBlendFilter() },
            FilterItem("GPUImageExposureFilter", listOf()) { GPUImageExposureFilter() },

            FilterItem("GPUImageFalseColorFilter", listOf()) { GPUImageFalseColorFilter() },

            FilterItem("GPUImageHalftoneFilter", listOf()) { GPUImageHalftoneFilter() },
            FilterItem("GPUImageHardLightBlendFilter", listOf()) { GPUImageHardLightBlendFilter() },
            FilterItem("GPUImageHazeFilter", listOf()) { GPUImageHazeFilter() },
            FilterItem("GPUImageHighlightShadowFilter", listOf()) { GPUImageHighlightShadowFilter() },
            FilterItem("GPUImageHueBlendFilter", listOf()) { GPUImageHueBlendFilter() },
            FilterItem("GPUImageHueFilter", listOf()) { GPUImageHueFilter() },

            FilterItem("GPUImageKuwaharaFilter", listOf()) { GPUImageKuwaharaFilter() },

            FilterItem("GPUImageLaplacianFilter", listOf()) { GPUImageLaplacianFilter() },
            FilterItem("GPUImageLevelsFilter", listOf()) { GPUImageLevelsFilter() },
            FilterItem("GPUImageLightenBlendFilter", listOf()) { GPUImageLightenBlendFilter() },
            FilterItem("GPUImageLinearBurnBlendFilter", listOf()) { GPUImageLinearBurnBlendFilter() },
            FilterItem("GPUImageLookupFilter", listOf()) { GPUImageLookupFilter() },
            FilterItem("GPUImageLuminanceFilter", listOf()) { GPUImageLuminanceFilter() },
            FilterItem("GPUImageLuminanceThresholdFilter", listOf()) { GPUImageLuminanceThresholdFilter() },
            FilterItem("GPUImageLuminosityBlendFilter", listOf()) { GPUImageLuminosityBlendFilter() },

//            FilterItem("GPUImageMixBlendFilter", listOf()) { GPUImageMixBlendFilter() },
            FilterItem("GPUImageMonochromeFilter", listOf()) { GPUImageMonochromeFilter() },
            FilterItem("GPUImageMultiplyBlendFilter", listOf()) { GPUImageMultiplyBlendFilter() },

            FilterItem("GPUImageNonMaximumSuppressionFilter", listOf()) { GPUImageNonMaximumSuppressionFilter() },
            FilterItem("GPUImageNormalBlendFilter", listOf()) { GPUImageNormalBlendFilter() },

            FilterItem("GPUImageOpacityFilter", listOf()) { GPUImageOpacityFilter() },
            FilterItem("GPUImageOverlayBlendFilter", listOf()) { GPUImageOverlayBlendFilter() },

            FilterItem("GPUImagePixelationFilter", listOf()) { GPUImagePixelationFilter() },
            FilterItem("GPUImagePosterizeFilter", listOf()) { GPUImagePosterizeFilter() },

            FilterItem("GPUImageRGBDilationFilter", listOf()) { GPUImageRGBDilationFilter() },
            FilterItem("GPUImageRGBFilter", listOf()) { GPUImageRGBFilter() },

            FilterItem("GPUImageSaturationBlendFilter", listOf()) { GPUImageSaturationBlendFilter() },
            FilterItem("GPUImageSaturationFilter", listOf()) { GPUImageSaturationFilter() },
            FilterItem("GPUImageScreenBlendFilter", listOf()) { GPUImageScreenBlendFilter() },
            FilterItem("GPUImageSepiaToneFilter", listOf()) { GPUImageSepiaToneFilter() },
            FilterItem("GPUImageSharpenFilter", listOf()) { GPUImageSharpenFilter() },
            FilterItem("GPUImageSketchFilter", listOf()) { GPUImageSketchFilter() },
            FilterItem("GPUImageSmoothToonFilter", listOf()) { GPUImageSmoothToonFilter() },
            FilterItem("GPUImageSobelEdgeDetectionFilter", listOf()) { GPUImageSobelEdgeDetectionFilter() },
            FilterItem("GPUImageSobelThresholdFilter", listOf()) { GPUImageSobelThresholdFilter() },
            FilterItem("GPUImageSoftLightBlendFilter", listOf()) { GPUImageSoftLightBlendFilter() },
            FilterItem("GPUImageSolarizeFilter", listOf()) { GPUImageSolarizeFilter() },
            FilterItem("GPUImageSourceOverBlendFilter", listOf()) { GPUImageSourceOverBlendFilter() },
            FilterItem("GPUImageSphereRefractionFilter", listOf()) { GPUImageSphereRefractionFilter() },
            FilterItem("GPUImageSubtractBlendFilter", listOf()) { GPUImageSubtractBlendFilter() },
            FilterItem("GPUImageSwirlFilter", listOf()) { GPUImageSwirlFilter() },

            FilterItem("GPUImageThresholdEdgeDetectionFilter", listOf()) { GPUImageThresholdEdgeDetectionFilter() },
            FilterItem("GPUImageToneCurveFilter", listOf()) { GPUImageToneCurveFilter() },
            FilterItem("GPUImageToonFilter", listOf()) { GPUImageToonFilter() },
            FilterItem("GPUImageTransformFilter", listOf()) { GPUImageTransformFilter() },
//            FilterItem("GPUImageAlphaBlendFilter", listOf()) { GPUImageTwoInputFilter() },
//            FilterItem("GPUImageAlphaBlendFilter", listOf()) { GPUImageTwoPassFilter() },
//            FilterItem("GPUImageAlphaBlendFilter", listOf()) { GPUImageTwoPassTextureSamplingFilter() },

            FilterItem("GPUImageVibranceFilter", listOf()) { GPUImageVibranceFilter() },
            FilterItem("GPUImageVignetteFilter", listOf()) { GPUImageVignetteFilter() },

            FilterItem("GPUImageWeakPixelInclusionFilter", listOf()) { GPUImageWeakPixelInclusionFilter() },
            FilterItem("GPUImageWhiteBalanceFilter", listOf()) { GPUImageWhiteBalanceFilter() },

            FilterItem("GPUImageZoomBlurFilter", listOf()) { GPUImageZoomBlurFilter() }
    )

    private inner class FloatMethod(val name: String, val max: Float = 1f, var value: Float = 0.5f, val min: Float = 0f)

    private inner class FilterItem<T : GPUImageFilter>(val name: String, val floatMethods: List<FloatMethod>, val initMethod: () -> T) {
        val filter by lazy { initMethod() }

        fun createLeftDrawer() {
            if (filter is GPUImageTwoInputFilter) {
                mActivity.randomPhoneImage {
                    (filter as GPUImageTwoInputFilter).bitmap = BitmapFactory.decodeStream(mActivity.contentResolver.openInputStream(it))
                }
            }

            val methods = filter::class.java.declaredMethods

            mActivity.leftDrawer.removeAllViews()

            for (method in methods) {
                val parameterTypes = method.parameterTypes
                if (method.returnType.name == "void" && Modifier.isPublic(method.modifiers) && parameterTypes.size == 1) {
//                        method.invoke(filter, )
                    when (parameterTypes[0].name) {
                        "float" -> {
                            val floatMethod = floatMethods.firstOrNull { it.name == method.name }
                                    ?: FloatMethod(method.name)

                            val itemView = mActivity.layoutInflater.inflate(R.layout.item_gpu_test_seekbar, mActivity.leftDrawer, false)
                            mActivity.leftDrawer.addView(itemView)

                            itemView.name.text = method.name
                            itemView.seekBar.apply {
                                setScale(0, 100)
                                progress = (((floatMethod.value - floatMethod.min) / (floatMethod.max - floatMethod.min)) * 100).toInt()

                                setOnChangeListener(object : RulingSeekbar.OnChangeListener {
                                    override fun onProgressChanged(seekBar: RulingSeekbar, progress: Int, fromUser: Boolean) {

                                    }

                                    override fun onStartTrackingTouch(seekBar: RulingSeekbar) {
                                    }

                                    override fun onStopTrackingTouch(seekBar: RulingSeekbar) {
                                        val value = if (floatMethod == null) seekBar.progress.toFloat()
                                        else seekBar.progress / 100f * (floatMethod.max - floatMethod.min) + floatMethod.min

                                        method.invoke(filter, value)
                                        itemView.name.text = "${method.name}:${value}"
                                        floatMethod.value = value

                                        mActivity.imageView.filter = filter
                                    }
                                })
                            }
                        }
                    }
                }
            }

            printMethods()
        }

        fun printMethods() {
            KLog.i(filter::class.java.name + ": " + name)
            filter::class.java.declaredMethods.forEach { method ->
                val parameterTypes = method.parameterTypes
                if (Modifier.isPublic(method.modifiers))
                    KLog.i(method.returnType.name + " " + method.name
                            + "(" + parameterTypes.joinToString { it.name } + ")")
            }
        }
    }
}