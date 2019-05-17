package com.kiven.sample

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.iflytek.cloud.*
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * Created by wangk on 2019/5/17.
 */
class AHXunfeiTest : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

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


        // http://doc.xfyun.cn/msc_android/%E9%A2%84%E5%A4%87%E5%B7%A5%E4%BD%9C.html
        val mAsr = getXunfei()
        addView("讯飞识别", View.OnClickListener { _ ->
            KGranting.requestPermissions(activity, 377, Manifest.permission.RECORD_AUDIO,
                    "录音") {
                if (it) {
                    val ret = mAsr.startListening(mRecognizerListener)
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("听写失败,错误码：$ret")
                    }
                }
            }
        })
    }

    /**
     * 初始讯飞
     * http://doc.xfyun.cn/msc_android/%E9%A2%84%E5%A4%87%E5%B7%A5%E4%BD%9C.html
     */
    private fun getXunfei(): SpeechRecognizer {
        SpeechUtility.createUtility(mActivity, SpeechConstant.APPID + "=5a15147f")

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