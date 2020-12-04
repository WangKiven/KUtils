package com.kiven.sample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.kutils.file.KFile
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.*
import com.sxb.kutils_ktx.util.main
import kotlinx.android.synthetic.main.activity_lauch.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import me.grantland.widget.AutofitHelper

class LauchActivity : KActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lauch)

        GlobalScope.main {
            delay(2000)
            startActivity(Intent(this@LauchActivity, MainActivity::class.java))
            finish()
        }

        rulingSeekbar.apply {
            setScale(5, 120)
            progress = 30;
            addNode(10, 0, true);
            addNode(30, 2, true);
            addNode(57, 1, true);
            addNode(85, 1, false);
            addNode(110, 2, true);
        }

        KLog.printDeviceInfo()

        AutofitHelper.create(et_auto)

        et_auto.setText(KNetwork.getIPAddress() ?: "")
        textView2.text = KString.fromHtml(getString(R.string.text_test, 5, 9))


        /*startActivity(Intent(this, ClickNotiActivity::class.java))
        finish()*/
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.item_path -> KGranting.requestPermissions(this, 345, Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间") { isSuccess ->
                if (isSuccess) {
                    KLog.i("" + KFile.createFile("tmp", ".img", getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE))!!.absolutePath)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        KLog.i("" + KFile.createFile("tmp", ".img", getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!)!!.absolutePath)
                    }
                    KLog.i("" + KFile.createFile("tmp", ".img", getDatabasePath("db"))!!.absolutePath)
                    KLog.i("" + KFile.createFile("tmp", ".img", cacheDir)!!.absolutePath)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        KLog.i("" + KFile.createFile("tmp", ".img", dataDir)!!.absolutePath)
                    }
                }
            }
        }
    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }*/
}
