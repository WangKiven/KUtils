package com.kiven.sample

import android.Manifest
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KGranting

class AHRecorderPlay : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        val scroll = NestedScrollView(activity)
        scroll.addView(flexboxLayout)
        setContentView(scroll)

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
            btn.isAllCaps = false
            flexboxLayout.addView(btn)
        }


        KGranting.requestPermissions(mActivity, 1001, arrayOf(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                arrayOf("录音", "存储")) {
            if (!it) {
                finish()
            }
        }

        addTitle("选择录音类型")
        /**
         * url: https://www.jianshu.com/p/de779d509e6c
         * 1. WAV 格式：录音质量高，但是压缩率小，文件大

        2. AAC 格式：相对于 mp3，AAC 格式的音质更佳，文件更小，有损压缩，一般苹果或者Android SDK4.1.2（API 16）及以上版本支持播放

        3. AMR 格式：压缩比比较大，但相对其他的压缩格式质量比较差，多用于人声，通话录音

        4. mp3 格式，使用 MediaRecorder 没有该音频格式输出。一些人的做法是使用 AudioRecord 录音，然后编码成 wav 格式，再转换成 mp3 格式
         */
        addView("WAV", View.OnClickListener { _ ->
            val recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)// 设置麦克风
            /*
         * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
         * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
         */
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}