package com.kiven.sample.push

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.heytap.mcssdk.PushManager
import com.heytap.mcssdk.callback.PushAdapter
import com.heytap.mcssdk.callback.PushCallback
import com.heytap.mcssdk.mode.ErrorCode
import com.huawei.hms.push.HmsMessaging
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.kiven.sample.util.showToast
import com.xiaomi.mipush.sdk.MiPushClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * Created by oukobayashi on 2019-12-31.
 * 小米推送文档：https://dev.mi.com/console/doc/detail?pId=41
 *
 * 华为推送文档：
 * https://developer.huawei.com/consumer/cn/doc/development/HMS-Library/push-sdk-integrate
 * https://developer.huawei.com/consumer/cn/doc/development/HMS-Guides/push-Preparations
 *
 * 极光推送：https://docs.jiguang.cn/jpush/client/Android/android_guide/
 *
 * iOS推送：https://github.com/notnoop/java-apns
 *
 * OPPO推送：https://open.oppomobile.com/wiki/doc#id=10196
 */
class AHPushTest : KActivityDebugHelper() {
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
        addTitle("小米推送")
        addView("注册", View.OnClickListener {
            // 小米手机不需要申请权限
            KGranting.requestPermissions(mActivity, 3344, arrayOf(
                    Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), arrayOf("拨号", "存储")) {
                if (it) {
                    MiPushHelper.initMiPush(mActivity)
                }
            }
        })
        addView("注销", View.OnClickListener { MiPushClient.unregisterPush(mActivity) })

        addView("禁用推送服务", View.OnClickListener { MiPushClient.disablePush(mActivity) })
        addView("启用推送服务", View.OnClickListener { MiPushClient.enablePush(mActivity) })

        addView("暂停接收推送", View.OnClickListener { MiPushClient.pausePush(mActivity, null) })
        addView("恢复接收推送", View.OnClickListener { MiPushClient.resumePush(mActivity, null) })

        addView("设置账号", View.OnClickListener { MiPushClient.setUserAccount(mActivity, "1", null) })
        addView("清除账号", View.OnClickListener { MiPushClient.unsetUserAccount(mActivity, "1", null) })

        addView("设置别名", View.OnClickListener { MiPushClient.setAlias(mActivity, "user", null) })
        addView("清除别名", View.OnClickListener { MiPushClient.unsetAlias(mActivity, "user", null) })

        addView("订阅topic", View.OnClickListener { MiPushClient.subscribe(mActivity, "topic1", null) })
        addView("取消topic", View.OnClickListener { MiPushClient.unsubscribe(mActivity, "topic1", null) })

        addView("打印RegId和Region", View.OnClickListener {
            KLog.i("RegId: ${MiPushClient.getRegId(mActivity)}")
            KLog.i("Region: ${MiPushClient.getAppRegion(mActivity)}")
        })

        addTitle("华为推送")
        /*
CP ID                       900086000026482528
验证公钥                        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlJaBMPl1UJf20sUfG5iqqtdR1YvWxnlHVd2Ngc87bZPCLn39qb6GjMVMkwd/OTr8lPV9Q/PPPD2qIeFeHw8vLHvbHzZSq47RSkEyZ2/zstq8JSvzuOW5EDCLy4A3Qfh5jvX3p0AexaE2Me394Uoz5HAx0rViW3xzI9CmB+k6nmr276DqnU2U582wPONGiZjHsKkP5fl48XnrUwntSjG95Qmrnko/jGi5RAWqgDuBp6mLz9HNBkfd6HjWp2CVdqZcXfjXhBGFOJJz/3qUSPMxEys/SupE/bItnWxOaGi0MfYLmn6IzaiCg1B8Gxnv3eWLaO8kFZETB4Ma0lRXS4cYXQIDAQAB
Client ID                   264311861338440768
Client 密钥                   AE82A87CD99145ABF663860CCDB37964F9A3A9BCF44E9A15AE5AD1EC96624D99
APP ID                      101560277
APP SECRET                  dc11929ebd170973da48aeee623b8c3904c134244908ad79c2ffcab23746b8ff
         */
        addView("注册", View.OnClickListener { GlobalScope.launch { HuaWeiPushHelper.initHuaWeiPush(mActivity) } })
        addView("注销", View.OnClickListener { HuaWeiPushHelper.unregisterPush(mActivity) })

        addView("订阅topic", View.OnClickListener { HuaWeiPushHelper.subscribe(mActivity, "topic1") })
        addView("取消topic", View.OnClickListener { HuaWeiPushHelper.unsubscribe(mActivity, "topic1") })

        addView("不显示通知栏消息", View.OnClickListener { HmsMessaging.getInstance(mActivity).turnOffPush() })
        addView("显示通知栏消息", View.OnClickListener { HmsMessaging.getInstance(mActivity).turnOnPush() })

        addTitle("OPPO推送")
        /**
         * https://open.oppomobile.com/wiki/doc#id=10196
         * appid: 30236357
         * appkey:09e71d4db52046768cf431af43f11579
         * appsecret:a1b2d2c0564d46e3b5319241bdeba7c1
         * appserversecret:aad0545c1b52498e98f3f3cc5e329d6a
         */
        addView("注册", View.OnClickListener {
            if (PushManager.isSupportPush(mActivity)) {
                OPPOPushHelper.initOPPOPush(mActivity)
            }else{
                KLog.i("不支持oppo推送")
            }
        })
        addView("注销", View.OnClickListener { PushManager.getInstance().unRegister() })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
    }
}