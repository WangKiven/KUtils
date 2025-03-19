package com.kiven.sample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.zxing.*
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KUtil
import java.util.*

class AHQrCode : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START
        flexboxLayout.fitsSystemWindows = true

        setContentView(flexboxLayout)

        val image = ImageView(mActivity)
        flexboxLayout.addView(image)

        val editText = EditText(mActivity)
        flexboxLayout.addView(editText)

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }

        // TODO: 2018/7/6 ---------------------------------------------
        addTitle("zxing: https://github.com/WangKiven/zxing")

        var bitmap: Bitmap? = null
        addView("创建", View.OnClickListener {
            var text = editText.text.toString()
            if (text.isEmpty()) {
                text = "好好学习，天天向上"
                editText.setText(text)
            }

            bitmap = encodeAsBitmap(text, false)
            image.setImageBitmap(bitmap)
        })
        addView("创建带图标", View.OnClickListener {
            var text = editText.text.toString()
            if (text.isEmpty()) {
                text = "好好学习，天天向上"
                editText.setText(text)
            }

            bitmap = encodeAsBitmap(text, true)
            image.setImageBitmap(bitmap)
        })
        addView("识别图片 ", View.OnClickListener {
            bitmap?.apply {
                val pixels = IntArray(width * height)
                getPixels(pixels, 0, width, 0, 0, width, height)


                val textr = MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(RGBLuminanceSource(
                        width, height, pixels))))
                if (textr != null) {
                    KAlertDialogHelper.Show1BDialog(mActivity, textr.text)
                } else {
                    KAlertDialogHelper.Show1BDialog(mActivity, "呀！没解析出来额。。。")
                }

            } ?: return@OnClickListener
        })

        addView("扫描", View.OnClickListener {

        })

        addTitle("\n\nzxing zbar 使用快捷使用库: https://github.com/WangKiven/BGAQRCode-Android")
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(text: String, withLogo: Boolean): Bitmap? {
        var hints: EnumMap<EncodeHintType, Any>? = null
        val encoding = guessAppropriateEncoding(text)
        if (encoding != null) {
            hints = EnumMap(EncodeHintType::class.java)
            hints[EncodeHintType.CHARACTER_SET] = encoding
        }
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, KUtil.dip2px(100f), KUtil.dip2px(100f), hints)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) Color.BLUE else Color.WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return if (withLogo) addLogoToQRCode(bitmap, BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)) else bitmap
    }

    private fun guessAppropriateEncoding(contents: CharSequence): String? {
        // Very crude at the moment
        for (i in 0 until contents.length) {
            if (contents[i].toInt() > 0xFF) {
                return "UTF-8"
            }
        }
        return null
    }

    /**
     * 添加logo到二维码图片上
     */
    private fun addLogoToQRCode(src: Bitmap, logo: Bitmap): Bitmap? {

        val srcWidth = src.width
        val srcHeight = src.height
        val logoWidth = logo.width
        val logoHeight = logo.height

        val scaleFactor = srcWidth * 1.0f / 5f / logoWidth.toFloat()
        var bitmap: Bitmap? = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(bitmap!!)
            canvas.drawBitmap(src, 0f, 0f, null)
            canvas.scale(scaleFactor, scaleFactor, (srcWidth / 2).toFloat(), (srcHeight / 2).toFloat())
            canvas.drawBitmap(logo, ((srcWidth - logoWidth) / 2).toFloat(), ((srcHeight - logoHeight) / 2).toFloat(), null)
            canvas.save()
            canvas.restore()
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap = null
        }

        return bitmap
    }
}