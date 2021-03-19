package com.kiven.sample.systemdata

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.tools.KGranting
import com.kiven.sample.util.showSnack
import com.kiven.sample.util.showTip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.support.v4.nestedScrollView
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by oukobayashi on 2019-11-12.
 * 参考demo：https://gitee.com/WangKiven/storage-samples/tree/master/MediaStore
 * 参考文档：https://www.jianshu.com/p/498c9d06c193
 */
class AHSysgemData : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

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
            flexboxLayout.addView(btn)
        }
        // TODO: 2019-11-12 ----------------------------------------------------------
        val txtTag = addTitle("媒体库")
        addView("查询相册", View.OnClickListener {
            AHSystemImage().startActivity(mActivity)
        })
        addView("查询视频", View.OnClickListener {
            loadData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATE_MODIFIED)
        })
        addView("查询音频", View.OnClickListener {
            loadData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATE_MODIFIED)
        })
        addView("查询下载", View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                loadData(MediaStore.Downloads.EXTERNAL_CONTENT_URI, MediaStore.Downloads.DATE_MODIFIED)
            else mActivity.showSnack("Android Q 开始支持")
        })

        addView("查询文件", View.OnClickListener {
            mActivity.showSnack("数据太多，注释掉代码了，不然打印半天")
//            loadData(MediaStore.Files.getContentUri("external"), MediaStore.Files.FileColumns.DATE_MODIFIED)
        })
        addTitle("通讯录")
        addView("查询通讯录", View.OnClickListener {
            loadData(ContactsContract.Contacts.CONTENT_URI, ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP)
        })
        addTitle("日历: https://www.jianshu.com/p/4820e02b2ee4")
        addView("查询日历", View.OnClickListener {
            // CalendarContract.Calendars.ENTERPRISE_CONTENT_URI
            KGranting.requestPermissions(mActivity, 788,
                    arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR),
                    arrayOf("日历添加", "日历获取")) {
                if (it) loadData(CalendarContract.Calendars.CONTENT_URI, CalendarContract.Calendars._ID)
            }
        })

        addView("调用日历功能", View.OnClickListener {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val begin = calendar.time.time
            calendar.add(Calendar.DAY_OF_YEAR, 2)
            val end = calendar.time.time

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "日历打磨")
                putExtra(CalendarContract.Events.EVENT_LOCATION, "福年广场")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
            }
            if (intent.resolveActivity(mActivity.packageManager) != null) {
                mActivity.startActivity(intent)
            }
        })


        addTitle("其他")
        addView("APN", View.OnClickListener {
            mActivity.showSnack("No permission to write APN settings")
//            loadData(Telephony.Carriers.CONTENT_URI, Telephony.Carriers._ID)
        })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
    }

    private fun loadData(uri: Uri, sortedKey: String) {
        GlobalScope.launch {
            val iDatas = mutableListOf<TreeMap<String, String>>()

            val hasP = suspendCoroutine<Boolean> { cc ->
                GlobalScope.launch(Dispatchers.Main) {
                    KGranting.requestPermissions(mActivity, 345,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                            arrayOf("内存", "通讯录读取", "通讯录修改")) {
                        cc.resume(it)
                    }
                }
            }

            if (!hasP) return@launch

            withContext(Dispatchers.IO) {
                val resolver = mActivity.contentResolver

                resolver.query(
                        uri,
                        null,
                        null,
                        null,
                        "$sortedKey desc"
                )
                        ?.use { cusor ->
                            val names = cusor.columnNames
                            val indexs = names.map { cusor.getColumnIndex(it) }


                            cusor.moveToFirst()
                            do {
//                                val map = TreeMap<String, String>()
                                val sb = StringBuilder()

                                for (i in names.indices) {
//                                    map[names[i]] = cusor.getString(indexs[i]) ?: ""
                                    sb.append(names[i]).append(" = ").append(cusor.getString(indexs[i])).append(", ")

                                }
//                                sb.append(MediaStore.Video.Media.DISPLAY_NAME).append(" = ").append(cusor.getString(cusor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)))
//                                iDatas.add(map)
                                showTip(sb.toString())
                            } while (cusor.moveToNext())

                            cusor.close()
                        }
            }
        }
    }
}