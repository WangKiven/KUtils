package com.kiven.sample.dock

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ImageView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAppTool
import com.kiven.kutils.tools.KUtil
import com.kiven.sample.R
import com.kiven.sample.util.showDialog
import kotlinx.android.synthetic.main.activity_dock.*
import kotlinx.android.synthetic.main.item_app.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.xutils.x
import java.io.IOException
import java.lang.ref.SoftReference
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by oukobayashi on 2019-08-09.
 *
 * 桌面检测与设置：https://blog.csdn.net/weixin_41549915/article/details/81633354
 * Android获取应用信息(AndroidManifest): https://www.jianshu.com/p/94dfcb869995
 */
@Route(path = "/dock/home")
class ActivityDock : KActivity() {

    private val apps: MutableList<EntityAppInfo> = ArrayList()
    private val appAdapter by lazy {
        AppAdapter()
    }

    private var textColor = Color.WHITE
        set(value) {
            field = value
            appAdapter.notifyDataSetChanged()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dock)
        loadBg()

        recyclerView.layoutManager = GridLayoutManager(this, 5)
        recyclerView.adapter = appAdapter
    }

    override fun onResume() {
        super.onResume()
        loadApps()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.apps, menu)
        menu.add(0, Menu.FIRST + 1, 0, "打开设置")
        menu.add(0, Menu.FIRST + 2, 0, "设置桌面")
        menu.add(0, Menu.FIRST + 3, 0, "当前桌面信息")
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            Menu.FIRST + 1 -> startActivity(Intent(Settings.ACTION_SETTINGS))
            Menu.FIRST + 2 -> {
                val paramIntent = Intent("android.intent.action.MAIN")
                paramIntent.component = ComponentName("android", "com.android.internal.app.ResolverActivity")
                paramIntent.addCategory("android.intent.category.DEFAULT")
                paramIntent.addCategory("android.intent.category.HOME")
                startActivity(paramIntent)
            }
            Menu.FIRST + 3 -> {
                // 判断桌面是否是当前app的
                val dockIntent = Intent(Intent.ACTION_MAIN)
                dockIntent.addCategory(Intent.CATEGORY_HOME)
                val res = packageManager.resolveActivity(dockIntent, 0) ?: return true

                val sb = StringBuilder()
                sb.appendln("桌面Activity类名：${res.activityInfo.name}")
                sb.appendln("桌面应用包名：${res.activityInfo.packageName}")

                showDialog(sb.toString())
            }
        }
        return true
    }

    private val wpTag = "wallpaper"

    private fun loadBg() {
        val num = KUtil.getSharedPreferencesIntValue(wpTag, 0)
        KUtil.putSharedPreferencesIntValue(wpTag, num + 1)

        try {
            val locales = assets.list(wpTag)
            if (locales == null || locales.isEmpty()) {
                return
            }

            val path = wpTag + "/" + locales[num % locales.size]

            val inputStream = assets.open(path)
            val bgBitmap = BitmapFactory.decodeStream(inputStream)

            val imageView = findViewById<View>(R.id.iv_bg) as ImageView
            imageView.setImageBitmap(bgBitmap)

            Palette.from(bgBitmap).resizeBitmapArea(8).generate { palette ->
                var swatch: Palette.Swatch? = null

                if (swatch == null) {
                    swatch = palette?.darkVibrantSwatch
                }
                if (swatch == null) {
                    swatch = palette?.darkMutedSwatch
                }

                if (swatch == null) {
                    swatch = palette?.lightVibrantSwatch
                }
                if (swatch == null) {
                    swatch = palette?.lightMutedSwatch
                }

                if (swatch == null) {
                    swatch = palette?.vibrantSwatch
                }
                if (swatch == null) {
                    swatch = palette?.mutedSwatch
                }


                textColor = swatch?.titleTextColor ?: Color.WHITE
            }

        } catch (e: IOException) {
            KLog.e(e)
        }

    }


    private fun loadApps() {
        GlobalScope.launch { }

        x.task().run(Runnable {
            apps.clear()


            /*Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getPackageManager();

        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL);
        if (resolveInfos != null && resolveInfos.size() > 0) {
            List<EntityAppInfo> appInfos = new ArrayList<>(resolveInfos.size());

            for (ResolveInfo resolveInfo : resolveInfos) {
                appInfos.add(new EntityAppInfo(packageManager, resolveInfo.activityInfo.applicationInfo));
            }

            apps.addAll(appInfos);
        }*/

            /*val resolveInfos = packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN, null), PackageManager.MATCH_ALL)
            resolveInfos.forEach {
                it.filter.categoriesIterator()
            }*/

            // 获取所有应用，包括系统服务
            val packageManager = packageManager

            val applicationInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            } else {
                packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES)
            }
            if (applicationInfos.size > 0) {
                for (applicationInfo in applicationInfos) {
                    apps.add(EntityAppInfo(packageManager, applicationInfo))
                }

                apps.sortWith(Comparator { o1, o2 ->
                    if (o1.canStart) {
                        if (o2.canStart) 0 else -1
                    } else {
                        if (o2.canStart) 1 else 0
                    }
                })
            }

            // TODO 调用主线程刷新列表
            x.task().post(Runnable { appAdapter.notifyDataSetChanged() })
        })
    }

    /**
     * 启动APP
     *
     * @param pkg 包名
     */
    private fun startApp(pkg: String) {
        KAppTool.startApp(this, pkg)
    }

    private inner class AppAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val appInfo = apps[position]

            holder.itemView.apply {
                val task = MyTask()
                task.imageView = SoftReference(iv_app_icon)
                task.appInfo = appInfo
                task.execute()


                tv_app_titile.text = appInfo.appTitle
                if (appInfo.canStart)
                    tv_app_titile.setTextColor(if (appInfo.isSystem) Color.RED else Color.BLUE)
                else
                    tv_app_titile.setTextColor(Color.GRAY)

                setOnClickListener {
                    appInfo.print()
                    startApp(appInfo.packageName)
                }
            }
        }

        override fun getItemCount(): Int = apps.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                object : RecyclerView.ViewHolder(LayoutInflater.from(this@ActivityDock).inflate(R.layout.item_app, parent, false)) {}
    }

    class MyTask : AsyncTask<Any, Drawable, Drawable>() {
        var imageView: SoftReference<ImageView>? = null
        var appInfo: EntityAppInfo? = null

        override fun doInBackground(params: Array<Any>): Drawable {
            return appInfo!!.appIcon
        }

        override fun onPostExecute(drawable: Drawable) {
            super.onPostExecute(drawable)
            imageView?.get()?.setImageDrawable(drawable)
        }
    }
}