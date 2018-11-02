package com.kiven.sample.xutils.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.sample.xutils.db.entity.User
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.createTable
import org.jetbrains.anko.db.select
import org.xutils.DbManager
import org.xutils.x

class AHDbDemo : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        val scroll = NestedScrollView(activity)
        scroll.addView(flexboxLayout)
        setContentView(scroll)

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
            btn.isAllCaps = false
            flexboxLayout.addView(btn)
        }

        val xDb = x.getDb(DbManager.DaoConfig().setDbName("xutils_demo.db")
                .setDbVersion(1)//16->4.66
                .setDbOpenListener { db ->
                    // 开启WAL, 对写入加速提升巨大
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        db.database.enableWriteAheadLogging()
                }
                .setDbUpgradeListener { db, oldVersion, newVersion ->
                    if (oldVersion < newVersion)
                        db?.apply {
                            dropTable(User::class.java)
                        }
                })

        val helper = object : SQLiteOpenHelper(mActivity, "ncustom.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase?) {
                db?.createTable("Boll", true, Pair("name", TEXT), Pair("date", TEXT))
            }

            override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            }
        }
        val cDb = helper.writableDatabase
        // TODO: 18-9-3 --------------------------------------------------

        addTitle("xutil")
        addView("save", View.OnClickListener {
            try {
                xDb.save(User(null, "kiven", "2017", "5678998989898987678"))
            } catch (e: Throwable) {
                KLog.e(e)
            }
        })
        addView("read", View.OnClickListener {
            try {
                xDb.findAll(User::class.java).forEach {
                    KLog.i(it.toString())
                }
            } catch (e: Throwable) {
                KLog.e(e)
            }
        })
        // TODO: 18-9-3 --------------------------------------
        addTitle("原生代码")
        addView("保存", View.OnClickListener {
            cDb.execSQL("INSERT INTO Boll(name,date) VALUES('lanboll', '今天')")

        })
        addView("查询", View.OnClickListener {
            cDb.select("Boll"/*, "name", "date"*/).exec {
                if (moveToFirst()) {
                    do {
                        KLog.i("name = ${getString(0)}, date = ${getString(1)}")
                    } while (moveToNext())
                }
            }
        })
        addView("删除所有表内容", View.OnClickListener {
            cDb.execSQL("DELETE FROM Boll")
        })

        addTitle("结构查询")
        addView("查询数据库结构", View.OnClickListener {
            cDb.select("sqlite_master").exec {
                KLog.i("count = $count")
                KLog.i("colo = ${columnNames.contentToString()}")
                if (moveToFirst()) {
                    do {

                        // 打印数据类型，name是String,对应的int为FIELD_TYPE_STRING = 3
                        KLog.i("name_type = ${getType(1)}")
                        // 打印值
                        KLog.i("type = ${getString(0)}," +
                                " name = ${getString(1)}," +
                                " tbl_name = ${getString(2)}," +
                                " rootpage = ${getString(3)}," +
                                " sql = ${getString(4)}")
                    } while (moveToNext())
                }
            }
        })
        addView("查询表结构", View.OnClickListener {
            // 有的表可能没有数据，所以不能用getType（），获取数据类型。索性就只获取属性
            cDb.select("Boll").limit(0).exec {
                KLog.i("colo = ${columnNames.contentToString()}")
            }
        })
    }
}