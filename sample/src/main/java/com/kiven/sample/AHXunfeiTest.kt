package com.kiven.sample

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognitionService
import android.speech.RecognizerIntent
import com.iflytek.cloud.*
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.kiven.kutils.tools.KString
import com.kiven.sample.noti.SpeechText
import com.kiven.sample.util.getInput
import com.kiven.sample.util.showBottomSheetDialog
import com.kiven.sample.util.showSnack
import kotlinx.android.synthetic.main.ah_xunfei_test.*

/**
 * Created by wangk on 2019/5/17.
 * 集成文档：https://www.xfyun.cn/doc/
 * api: http://mscdoc.xfyun.cn/android/api/
 */
class AHXunfeiTest : KActivityHelper() {
    val text = """
        北国风光，千里冰封，万里雪飘。
        望长城内外，惟余莽莽；大河上下，顿失滔滔。
        山舞银蛇，原驰蜡象，欲与天公试比高。
        须晴日，看红装素裹，分外妖娆。
        江山如此多娇，引无数英雄竞折腰。
        惜秦皇汉武，略输文采；唐宗宋祖，稍逊风骚。
        一代天骄，成吉思汗，只识弯弓射大雕。
        俱往矣，数风流人物，还看今朝。
    """

    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        setContentView(R.layout.ah_xunfei_test)

        activity.apply {
            var voiceName = "xiaoyan"
            btn_voice_name.text = "发音人($voiceName)"
            btn_voice_name.setOnClickListener {
                activity.showBottomSheetDialog(listOf(
                        "讯飞小燕",
                        "讯飞许久",
                        "讯飞小萍",
                        "讯飞小婧",
                        "讯飞许小宝",
                        "讯飞马叔(3-12后需续费)"
                )) { index, _ ->
                    voiceName = listOf("xiaoyan", "aisjiuxu", "aisxping", "aisjinger", "aisbabyxu", "x_laoma")[index]
                    btn_voice_name.text = "发音人($voiceName)"
                }
            }

            var voiceSpeed = 50
            btn_voice_speed.text = "语速($voiceSpeed)"
            btn_voice_speed.setOnClickListener {
                activity.getInput("语速 0~100", voiceSpeed.toString()) {
                    voiceSpeed = KString.toInt(it.toString(), voiceSpeed)
                    btn_voice_speed.text = "语速($voiceSpeed)"
                }
            }

            var voiceVolume = 50
            btn_voice_volume.text = "音量($voiceVolume)"
            btn_voice_volume.setOnClickListener {
                activity.getInput("音量 0~100", voiceVolume.toString()) {
                    voiceVolume = KString.toInt(it.toString(), voiceVolume)
                    btn_voice_volume.text = "音量($voiceVolume)"
                }
            }

            var voicePitch = 50
            btn_voice_pitch.text = "语调($voiceName)"
            btn_voice_pitch.setOnClickListener {
                activity.getInput("语调 0~100", voicePitch.toString()) {
                    voicePitch = KString.toInt(it.toString(), voicePitch)
                    btn_voice_pitch.text = "语调($voicePitch)"
                }
            }

            var voiceBg = 0
            btn_voice_bg.text = "背景音乐($voiceBg)"
            btn_voice_bg.setOnClickListener {
                voiceBg = if (voiceBg == 0) 1 else 0
                btn_voice_bg.text = "背景音乐($voiceBg)"
            }

            var voiceFading = false
            btn_voice_fading.text = "淡入淡出($voiceFading)"
            btn_voice_fading.setOnClickListener {
                voiceFading = !voiceFading
                btn_voice_fading.text = "淡入淡出($voiceFading)"
            }


            // 识别语音
            val mAsr = getXunfei()
            btn_listen_voice.setOnClickListener {
                val ret = mAsr.startListening(mRecognizerListener)
                if (ret != ErrorCode.SUCCESS) {
                    showTip("听写失败,错误码：$ret")
                }
            }
            // 语音合成
            val mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(activity) {}
            btn_create_voice.setOnClickListener {
                mapOf(
                        SpeechConstant.VOICE_NAME to voiceName,// 合成发音人, 默认值：xiaoyan
                        SpeechConstant.SPEED to "$voiceSpeed",// 语速, 默认值50, 值范围：[0, 100]
                        SpeechConstant.VOLUME to "$voiceVolume",// 音量, 默认值50, 值范围：[0, 100]
                        SpeechConstant.PITCH to "$voicePitch",// 语调, 默认值50, 值范围：[0, 100]
                        SpeechConstant.BACKGROUND_SOUND to "$voiceBg",// 背景音乐, 默认值0, 值范围：{ null, 0, 1 }
                        SpeechConstant.TTS_FADING to "$voiceFading"// 是否在合成播放开始、暂停和恢复时，进行声音的淡入淡出。 默认值false, 值范围：{ null, true，false }
                ).forEach {
                    mSpeechSynthesizer.setParameter(it.key, it.value)
                }
                mSpeechSynthesizer.startSpeaking(text, object : SynthesizerListener {
                    override fun onBufferProgress(p0: Int, p1: Int, p2: Int, p3: String?) {}
                    override fun onSpeakBegin() {}
                    override fun onSpeakProgress(p0: Int, p1: Int, p2: Int) {}
                    override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {}
                    override fun onSpeakPaused() {}
                    override fun onSpeakResumed() {}
                    override fun onCompleted(p0: SpeechError?) {}
                })
            }

            // 声纹识别
            btn_read_voice.setOnClickListener {

            }

            /*val component = Settings.Secure.getString(contentResolver, "voice_recognition_service")
            val mSystemSr = android.speech.SpeechRecognizer.createSpeechRecognizer(activity,
                    ComponentName.unflattenFromString(component))*/
            val mSystemSr = android.speech.SpeechRecognizer.createSpeechRecognizer(activity)
            mSystemSr.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onBeginningOfSpeech() {
                    showTip("开始说话")
                }

                override fun onEndOfSpeech() {
                    showTip("结束说话")
                }

                override fun onError(error: Int) {
                    showTip("系统语音识别异常 $error")
                }
                override fun onResults(results: Bundle?) {
                    KLog.i("系统语音识别结果：")
                    results?.keySet()?.forEach {
                        KLog.i("  $it = ${results.get(it)}")
                    }
                }
            })
            btn_system_listen_voice.setOnClickListener {
                if (!android.speech.SpeechRecognizer.isRecognitionAvailable(activity)) {
                    showSnack("系统说没有语言识别服务")
                    return@setOnClickListener
                }

                mSystemSr.startListening(
                        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                            //putExtra(RecognizerIntent.EXTRA_LANGUAGE, )
                        }
                )
            }
            btn_system_listen_voice_stop.setOnClickListener {
                mSystemSr.stopListening()// 停止监听
//                mSystemSr.cancel()// 取消服务
            }

            btn_system_create_voice.setOnClickListener {
//                showSnack("在监听通知的类MyNotificationListenerService有使用，这里就不写了")
                SpeechText.speech(text)
            }
        }
    }

    /**
     * 初始讯飞
     */
    private fun initXunfei() {
        SpeechUtility.createUtility(mActivity, SpeechConstant.APPID + "=5a15147f")
    }

    /**
     * 获取识别接口
     * http://doc.xfyun.cn/msc_android/%E9%A2%84%E5%A4%87%E5%B7%A5%E4%BD%9C.html
     */
    private fun getXunfei(): SpeechRecognizer {
        initXunfei()

        val mAsr = SpeechRecognizer.createRecognizer(mActivity) { code ->
            KLog.i("SpeechRecognizer init() code = $code")
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：$code")
            }

        }
        // 设置引擎类型
        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mAsr.setParameter(SpeechConstant.CLOUD_GRAMMAR, null)
        mAsr.setParameter(SpeechConstant.SUBJECT, null)

        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8")

        /*val dia = RecognizerDialog(mActivity, { code ->
            KLog.i("SpeechRecognizer init() code = $code")
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：$code")
            }
        })
        dia.show()*/

        return mAsr
    }

    val mTranslateEnable = false
    private val mRecognizerListener = object : RecognizerListener {

        override fun onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话")
        }

        override fun onError(error: SpeechError) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (mTranslateEnable && error.errorCode == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能")
            } else {
                showTip(error.getPlainDescription(true))
            }
        }

        override fun onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话")
        }

        override fun onResult(results: RecognizerResult, isLast: Boolean) {
            KLog.i(results.resultString)
            if (mTranslateEnable) {
                printTransResult(results)
            } else {
                printResult(results)
            }

            if (isLast) {
                // TODO 最后的结果
            }
        }

        override fun onVolumeChanged(volume: Int, data: ByteArray) {
            KLog.i("当前正在说话，音量大小：$volume")
            KLog.i("返回音频数据：" + data.size)
        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    }


    private fun printTransResult(results: RecognizerResult) {
        KLog.d("结果t：${results.resultString}")
    }

    private fun printResult(results: RecognizerResult) {
        KLog.d("结果：${results.resultString}")
    }

    private fun showTip(word: String) {
//        KAlertDialogHelper.Show1BDialog(mActivity, word)
        KLog.d(word)
    }
}