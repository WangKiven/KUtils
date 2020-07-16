package com.kiven.sample.libs

import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import com.kiven.sample.util.randomPhoneImage
import com.kiven.sample.util.showBottomSheetDialog
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter
import kotlinx.android.synthetic.main.ah_gpu_image_lib.*

/**
 * Created by oukobayashi on 2020/7/16.
 */
class AHGPUImageLib : KActivityDebugHelper() {
    private var imageUri: Uri? = null
        set(value) {
            field = value
            mActivity.imageView.setImage(value)
        }

    private var filterItem: FilterItem<*>? = null
    set(value) {
        val b = field != value
        field = value
        if (b) mActivity.imageView.filter = value?.initMethod?.invoke()
    }

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_gpu_image_lib)
        activity.apply {
            imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE)
            imageView.filter = GPUImageHueFilter()
            filterItem = filters[0]
            randomPhoneImage { imageUri = it }

            button.setOnClickListener {
                showBottomSheetDialog(listOf(
                        "修改属性",
                        "随机图片",
                        "选择图片",
                        "选择filter"
                )) { p, _ ->
                    when (p) {
                        0 -> {
                            drawer.openDrawer(Gravity.LEFT)
                        }
                        1 -> {
                        }
                        2 -> {
                        }
                        3 -> {
                        }
                    }
                }
            }
        }
    }

    private val filters = listOf<FilterItem<*>>(
            FilterItem("GPUImageHueFilter", { GPUImageHueFilter() })
    )

    private class FilterItem<T : GPUImageFilter>(val name: String, val initMethod: () -> T)
}