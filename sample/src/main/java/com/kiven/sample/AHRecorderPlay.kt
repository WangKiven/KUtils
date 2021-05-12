package com.kiven.sample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.format.Formatter
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KToast
import java.io.File
import java.util.*

class AHRecorderPlay : KActivityHelper() {
    var tvFb:TextView? = null

    @SuppressLint("ResourceType")
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        val scroll = NestedScrollView(activity)
        scroll.addView(flexboxLayout)
        setContentView(scroll)

        val addTitle = fun(text: String): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)

            return tv
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

        val addRadio = fun(rg: RadioGroup, text: String, rid: Int) {
            val rb = RadioButton(mActivity)
            rb.text = "$text $rid"
            rb.id = rid
            rg.addView(rb)
        }
        addTitle("选择音频源")
        val sourceGroup = RadioGroup(mActivity)
//        sourceGroup.orientation = LinearLayout.HORIZONTAL
        addRadio(sourceGroup, "DEFAULT", 0)
        addRadio(sourceGroup, "MIC", 1)
        addRadio(sourceGroup, "VOICE_COMMUNICATION(去回音)", 7)
        sourceGroup.check(1)
        flexboxLayout.addView(sourceGroup)

        addTitle("选择录音编码")
        val encodeGroup = RadioGroup(mActivity)
        /*encodeGroup.orientation = LinearLayout.HORIZONTAL*/
        addRadio(encodeGroup, "DEFAULT", 0)
        addRadio(encodeGroup, "AMR_NB", 1)
        addRadio(encodeGroup, "AMR_WB", 2)
        addRadio(encodeGroup, "AAC", 3)
        addRadio(encodeGroup, "HE_AAC", 4)
        addRadio(encodeGroup, "AAC_ELD", 5)
        addRadio(encodeGroup, "VORBIS", 6)
        encodeGroup.check(3)
        flexboxLayout.addView(encodeGroup)

        addTitle("选择录音输出格式")
        val oupGroup = RadioGroup(mActivity)
        /*oupGroup.orientation = LinearLayout.HORIZONTAL*/
        addRadio(oupGroup, "DEFAULT", 0)
        addRadio(oupGroup, "THREE_GPP", 1)
        addRadio(oupGroup, "MPEG_4", 2)
        addRadio(oupGroup, "RAW_AMR/AMR_NB(要求编码为AMR_NB)", 3)
        addRadio(oupGroup, "AMR_WB", 4)
        addRadio(oupGroup, "AAC_ADIF", 5)
        addRadio(oupGroup, "AAC_ADTS", 6)
        addRadio(oupGroup, "OUTPUT_FORMAT_RTP_AVP(hide)", 7)
        addRadio(oupGroup, "MPEG_2_TS", 8)
        addRadio(oupGroup, "WEBM(编码为VP8/VORBIS的输出格式)", 9)
        oupGroup.check(6)
        flexboxLayout.addView(oupGroup)


        tvFb = addTitle("")
        addTitle("操作（MediaRecorder）")

        addView("开始录音", View.OnClickListener { startRecord(sourceGroup.checkedRadioButtonId, encodeGroup.checkedRadioButtonId, oupGroup.checkedRadioButtonId) })

        addView("停止录音", View.OnClickListener { stopRecord() })

        addView("播放录音", View.OnClickListener { _ ->
            if (recorder != null) {
                KToast.ToastMessage("请先停止录音")
                return@OnClickListener
            }

            val dir = dir()
            if (dir.exists() && dir.isDirectory) {
                val files = dir.listFiles()
                if (files != null && files.isNotEmpty()) {
                    val items = Array<String>(files.size) {
                        val file = files[it]

                        "${file.name} - ${Formatter.formatFileSize(mActivity, file.length())}"
                    }
                    Arrays.sort(items) { o1: String, o2: String ->
                        return@sort o2.compareTo(o1)
                    }
                    val dialog = AlertDialog.Builder(mActivity)
                    dialog.setItems(items) { _, which ->
                        val player = MediaPlayer()
                        player.setDataSource(files[which].absolutePath)
                        player.setOnCompletionListener {
                            player.release()
                        }
                        player.prepare()
                        player.start()
                    }
                    dialog.show()
                } else {
                    KToast.ToastMessage("没找到录音文件")
                }
            } else {
                KToast.ToastMessage("没找到文件夹")
            }
        })

        addTitle("MediaRecorder不能输出mp3。要输出mp3的话，可采用AudioRecorder + Lame 的方案。" +
                "\n参考文档：https://www.jianshu.com/p/047b573a9ac4" +
                "\nAudioTrack: 可用于处理音频" +
                "\nringdroid（apk源码），录制和编辑声音，并创建铃声：https://github.com/google/ringdroid" +
                "\nmp3agic, 读取MP3文件并可修改MP3信息的java库：https://github.com/mpatric/mp3agic" +
                "\nMP3音频录制,可单边或者双边波形显示：https://github.com/CarGuo/GSYRecordWave")

    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }*/

    private fun dir(): File {
        return mActivity.getDir(Environment.DIRECTORY_MOVIES, Context.MODE_PRIVATE)
    }


    var recorder: MediaRecorder? = null
    /**
     * url: https://www.jianshu.com/p/de779d509e6c
     * url: https://www.jianshu.com/p/96ee1b7e67e3
     */
    private fun startRecord(source: Int, encoder: Int, ouputFormat: Int) {
        /**
         *
         * 1. WAV 格式：录音质量高，但是压缩率小，文件大
         * 2. AAC 格式：相对于 mp3，AAC 格式的音质更佳，文件更小，有损压缩，一般苹果或者Android SDK4.1.2（API 16）及以上版本支持播放
         * 3. AMR 格式：压缩比比较大，但相对其他的压缩格式质量比较差，多用于人声，通话录音
         * 4. mp3 格式，使用 MediaRecorder 没有该音频格式输出。一些人的做法是使用 AudioRecord 录音，然后编码成 wav 格式，再转换成 mp3 格式
         */
        if (recorder == null) {
            val fileName = "${System.currentTimeMillis()}e${encoder}o$ouputFormat"
            val filePath = File(dir(), fileName)


            recorder = MediaRecorder()
            recorder?.apply {
                /**
                 * 设置音频的来源，MIC(主麦克风),DEFAULT(默认)
                 * 打电话时：VOICE_DOWNLINK/VOICE_UPLINK/VOICE_CALL(只能听到别人的/自己/双方的声音)
                 */
//                setAudioSource(MediaRecorder.AudioSource.MIC)// 设置麦克风
                setAudioSource(source)
                /**
                 * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
                 * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
                 */
//                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFormat(ouputFormat)
                /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
//                setOutputFormat(MediaRecorder.AudioEncoder.AAC)
                setAudioEncoder(encoder)
                setOutputFile(filePath.absolutePath)
                prepare()
                start()

                dbHandler.sendEmptyMessageDelayed(0, 100)
            }

            KToast.ToastMessage("开始录音")
        } else {
            KToast.ToastMessage("请先停止录音")
        }
    }

    private fun stopRecord() {
        recorder?.apply {
            try {
                stop()
            } catch (e: Exception) {
                reset()
            }
            release()

            KToast.ToastMessage("已停止录音")
        }

        recorder = null
    }

    override fun onStop() {
        stopRecord()
        super.onStop()
    }

    private val dbHandler = Handler(Handler.Callback {
        printDb()
        return@Callback true
    })

    /**
     * 打印分贝
     * 分贝自测表: https://baike.baidu.com/item/%E5%88%86%E8%B4%9D/553473?fr=aladdin
     * 城市区域环境噪声标准: https://baike.baidu.com/item/%E5%9F%8E%E5%B8%82%E5%8C%BA%E5%9F%9F%E7%8E%AF%E5%A2%83%E5%99%AA%E5%A3%B0%E6%A0%87%E5%87%86/2420721?fr=aladdin
     */
    private fun printDb() {
        recorder?.apply {
            try {
                val ratio = maxAmplitude
                if (ratio > 0) {
                    val fb = "分贝：${20 * Math.log10(ratio.toDouble())}"
                    KLog.i(fb)
                    tvFb?.text = fb;
                }
            } catch (e: Exception) {
                KLog.e(e)
            }
            dbHandler.sendEmptyMessageDelayed(0, 200)
        }
    }
}