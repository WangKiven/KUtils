package com.kiven.sample.ax

import android.os.Bundle
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.BaseFlexActivityHelper
import com.kiven.sample.compose.AHComposeDemo
import com.kiven.sample.media.AHMediaList
import com.kiven.sample.util.addBtn
import com.kiven.sample.util.dataStore
import com.kiven.sample.util.showDialog
import com.kiven.sample.util.showToast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Date

class AHAxDemo : BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)



        /// https://developer.android.google.cn/topic/libraries/architecture/datastore?hl=zh-cn#kotlin
        addTitle("DataStore-简单数据存储-替代SharedPreferences。也支持proto数据，这里不做测试。")
        addBtn("String数据") {
            runBlocking {
                val stringKey = stringPreferencesKey("AHAxDemo_stringKey")
                mActivity.dataStore.edit {
                    it[stringKey] = "String数据 " + Date().toString()
                }

                val f = mActivity.dataStore.data.map { it[stringKey] ?: "无" }.first()
                mActivity.showDialog("f = ${f}")
            }
        }
        addBtn("int数据") {
            runBlocking {
                val intKey = intPreferencesKey("AHAxDemo_intKey")
                var v = -1
                mActivity.dataStore.edit {
                    val vv = it[intKey] ?: 0
                    v = vv + 1
                    it[intKey] = v
                }
                val f = mActivity.dataStore.data.map { it[intKey] ?: 0 }.first()
                mActivity.showDialog("v = ${v}, f = ${f}")
            }
        }

        // https://developer.android.google.cn/develop/background-work/background-tasks/persistent/getting-started?hl=zh-cn
        addTitle("WorkManager 持久性工作, 替代 JobScheduler")
        addBtn("简单任务") {
            showToast()
        }

        addTitle("更多")
        addBtn("照片选择器") {
            AHMediaList().startActivity(mActivity)
        }
        addBtn("compose") { AHComposeDemo().startActivity(mActivity) }
    }
}