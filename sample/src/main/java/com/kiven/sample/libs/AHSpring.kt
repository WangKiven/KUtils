package com.kiven.sample.libs

import android.os.Bundle
import android.view.View
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.BaseFlexActivityHelper
import org.springframework.http.converter.StringHttpMessageConverter

import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset


/**
 * Created by oukobayashi on 2020/7/22.
 */
class AHSpring : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        addBtn("", View.OnClickListener {
            Thread {
                try {
                    val url = "https://www.baidu.com"
                    val restTemplate = RestTemplate()
                    restTemplate.messageConverters.add(StringHttpMessageConverter(Charset.defaultCharset()))
                    val result = restTemplate.getForObject(url, String::class.java, "Android")
                    KLog.i(result)
                } catch (t: Throwable) {
                    KLog.e(t)
                }
            }.start()
        })
    }
}