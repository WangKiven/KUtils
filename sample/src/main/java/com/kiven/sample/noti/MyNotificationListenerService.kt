package com.kiven.sample.noti

import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.RequiresApi
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.util.showTip
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by oukobayashi on 2019-12-11.
 * https://blog.csdn.net/xiayiye5/article/details/83688396
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MyNotificationListenerService : NotificationListenerService() {
    var text2speech: TextToSpeech? = null

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.apply {
            KLog.printClassField(notification, null, true)

            val text = notification.tickerText

            showTip("监听到通知：${text}")

            // 不判空华为崩溃，小米没有崩溃
            if (text.isNullOrBlank()) {
                return
            }

            val exceptionHandler = CoroutineExceptionHandler { _, error ->
                KLog.i("文字转语音异常")
                KLog.e(error)
            }

            GlobalScope.launch(exceptionHandler) {
                initTextToSpeech()

                // QUEUE_ADD 添加新的，QUEUE_FLUSH 刷新(即清除之前的，会打断正在播放的)
                text2speech?.speak(text.toString(), TextToSpeech.QUEUE_ADD, null)
            }
        }
    }

    @Synchronized
    suspend fun initTextToSpeech() {
        if (text2speech == null) {
            val result = suspendCoroutine<Boolean> {
                text2speech = TextToSpeech(this@MyNotificationListenerService, TextToSpeech.OnInitListener { status ->
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
    }

    /*override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
        rankingMap?.orderedKeys?.forEach {
            val ranking = Ranking()
            rankingMap.getRanking(it, ranking)
            ranking.importanceExplanation
        }
    }*/
}