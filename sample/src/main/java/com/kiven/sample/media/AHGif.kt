package com.kiven.sample.media

import android.os.Bundle
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.R
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView
import java.io.BufferedInputStream

/**
 * Created by wangk on 2018/3/13.
 */
class AHGif : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity?, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        setContentView(R.layout.ah_gif)
        val gifImageView = findViewById<GifImageView>(R.id.gifImageView)

        val sourceIs = resources.openRawResource(R.raw.gif_loading)
//        val bis = BufferedInputStream(sourceIs)

        gifImageView.setImageDrawable(GifDrawable(sourceIs))
    }
}