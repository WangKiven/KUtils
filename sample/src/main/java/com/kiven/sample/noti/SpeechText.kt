package com.kiven.sample.noti

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KContext
import com.kiven.kutils.tools.KUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object SpeechText {
    private var text2speech: TextToSpeech? = null

    fun speech(text: String?) {
        if (text.isNullOrBlank()) {
            return
        }

        val exceptionHandler = CoroutineExceptionHandler { _, error ->
            KLog.i("文字转语音异常")
            KLog.e(error)
        }

        GlobalScope.launch(exceptionHandler) {
            if (text2speech == null) {
                val result = suspendCoroutine<Boolean> {
                    text2speech = TextToSpeech(KUtil.getApp(), TextToSpeech.OnInitListener { status ->
                        if (status == TextToSpeech.SUCCESS) {
                            it.resume(true)
                            return@OnInitListener
                        }
                        it.resume(false)
                    })
                }

                if (result) {
                    text2speech?.apply {
                        val supported = setLanguage(Locale.SIMPLIFIED_CHINESE)
                        if (supported != TextToSpeech.LANG_AVAILABLE && supported != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                            KLog.i("不支持当前语言！")
                        }
                    }
                    // TODO utteranceId即为 textToSpeech.speak("","",null,i)最后一个参数i
                    text2speech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onDone(utteranceId: String?) {

                        }

                        override fun onError(utteranceId: String?) {
                        }

                        override fun onStart(utteranceId: String?) {
                        }
                    })
                } else {
                    text2speech?.stop()// 不管是否正在朗读TTS都被打断
                    text2speech?.shutdown()// 关闭，释放资源
                    text2speech = null
                }
            }

            initTextToSpeech()

            // QUEUE_ADD 添加新的，QUEUE_FLUSH 刷新(即清除之前的，会打断正在播放的)
            text2speech?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    @Synchronized
    fun initTextToSpeech() {}
}