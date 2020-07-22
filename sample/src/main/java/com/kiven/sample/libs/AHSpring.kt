package com.kiven.sample.libs

import android.os.Bundle
import android.view.View
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.util.showToast
import org.springframework.http.ContentCodingType
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate


/**
 * Created by oukobayashi on 2020/7/22.
 */
class AHSpring : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val url = "https://www.baidu.com"

        addBtn("简单请求", View.OnClickListener {
            Thread {
                try {
                    val restTemplate = RestTemplate()
//                    restTemplate.messageConverters.add(StringHttpMessageConverter(Charset.defaultCharset()))
                    val result = restTemplate.getForObject(url, String::class.java, "Android")
                    KLog.i(result)
                } catch (t: Throwable) {
                    KLog.e(t)
                }
            }.start()
        })

        addBtn("GZIP 和 Header", View.OnClickListener {
            Thread {
                try {
                    val requestHeaders = HttpHeaders()
                    // todo 默认就使用了gzip的，所以没必要加这个。
                    //  如果想禁用gzip，需要设置requestHeaders.setAcceptEncoding(ContentCodingType.IDENTITY)
                    requestHeaders.setAcceptEncoding(ContentCodingType.GZIP)
                    val requestEntity: HttpEntity<*> = HttpEntity<Any>(requestHeaders)

                    val restTemplate = RestTemplate()

                    val response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String::class.java)
                    KLog.i(response.body)
                } catch (t: Throwable) {
                    KLog.e(t)
                }
            }.start()
        })

        // https://docs.spring.io/spring-android/docs/2.0.0.M3/reference/html/rest-template.html#d5e373
        addBtn("xml/json解析", View.OnClickListener {
            showToast("请查看代码注释的文档链接")
        })
        // https://docs.spring.io/spring-android/docs/2.0.0.M3/reference/html/rest-template.html#d5e395
        addBtn("发送json数据", View.OnClickListener {
            showToast("请查看代码注释的文档链接")
        })

        // https://docs.spring.io/spring-android/docs/2.0.0.M3/reference/html/rest-template.html#d5e405
        addBtn("登录", View.OnClickListener {
            showToast("请查看代码注释的文档链接")
        })

        // https://docs.spring.io/spring-android/docs/2.0.0.M3/reference/html/auth.html
        // 需要导入额外的库，目前没导入。这个功能似乎完全没什么用处
        addBtn("三方登录", View.OnClickListener {
            showToast("请查看代码注释的文档链接")
        })
    }
}