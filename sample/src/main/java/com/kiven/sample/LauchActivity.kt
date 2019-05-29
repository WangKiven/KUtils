package com.kiven.sample

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.kiven.kutils.activityHelper.KFragmentActivity
import com.kiven.kutils.activityHelper.activity.KActivity
import com.kiven.kutils.callBack.CallBack
import com.kiven.kutils.file.KFile
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.*
import com.kiven.kutils.widget.KNormalItemView
import com.kiven.sample.floatView.ActivityHFloatView
import com.kiven.sample.gl.AHGL
import com.kiven.sample.libs.AHLibs
import com.kiven.sample.media.AHMediaList
import com.kiven.sample.util.callPhone
import com.kiven.sample.util.snackbar
import kotlinx.android.synthetic.main.activity_lauch.*
import me.grantland.widget.AutofitHelper
import java.text.DateFormat
import java.util.*

class LauchActivity : KActivity() {

    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lauch)

        KUtil.printDeviceInfo()

        setupWindowAnimations()

        AutofitHelper.create(et_auto)


        iv_test.setOnClickListener(object : View.OnClickListener {
            var count = 0

            override fun onClick(v: View) {
                val urls = arrayOf("http://b.hiphotos.baidu.com/image/pic/item/908fa0ec08fa513db777cf78376d55fbb3fbd9b3.jpg", "http://file5.gucn.com/file2/ShopLogoFile/20120413/Gucn_20120413327888131819Logo.jpg", "/storage/emulated/0/DCIM/Camera/1557910396757.jpg")
                /*ImageOptions options = new ImageOptions.Builder()
                        .setCircular(true)
                        .setAutoRotate(true).setFadeIn(true).build();
                x.image().bind(iv_test, urls[count%urls.length], options);*/


                Glide.with(this@LauchActivity).load(urls[count % urls.size]).circleCrop().into(iv_test)
                count++
            }
        })
        textView2.text = KString.fromHtml(getString(R.string.text_test, 5, 9))



        val addTitle = fun(text: String) {
            val tv = TextView(this)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flex.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(this)
            btn.text = text
            btn.setOnClickListener(click)
            flex.addView(btn)
        }

        addView("小功能", View.OnClickListener { AHSmallAction().startActivity(this) })
        addView("悬浮框", View.OnClickListener { ActivityHFloatView().startActivity(this) })
        addView("opengl", View.OnClickListener { AHGL().startActivity(this) })
        addView("cpu、内存管理", View.OnClickListener { AHCPUMemory().startActivity(this) })
        addView("测试KActivityHelper", View.OnClickListener { ActivityHTestBase().startActivity(this) })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
        addView("", View.OnClickListener {  })
    }

    private fun setupWindowAnimations() {
        // Re-enter transition is executed when returning to this activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val slideTransition = Slide()
            slideTransition.slideEdge = Gravity.LEFT
            slideTransition.duration = 500
            window.reenterTransition = slideTransition
            window.exitTransition = slideTransition
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.item_libs -> AHLibs().startActivity(this)
            R.id.item_live_data -> {
                val handler = Handler(Handler.Callback {
                    KView.runUI(this@LauchActivity, CallBack { KAlertDialogHelper.Show1BDialog(this@LauchActivity, "LiveData 行不行？") })
                    true
                })
                handler.sendEmptyMessageDelayed(0, 5000)
            }
            R.id.item_fragment_proxy -> {
                val fproxyIntent = Intent(this, KFragmentActivity::class.java)
                fproxyIntent.putExtra("fragment_name", FragmentApple::class.java.name)
                startActivity(fproxyIntent)
            }
            R.id.item_setings -> startActivity(Intent(Settings.ACTION_SETTINGS))
            R.id.item_phone -> KGranting.requestPermissions(this, 101, Manifest.permission.CALL_PHONE, "拨号") { isSuccess ->
                val phoneno = "17012347428"
                if (isSuccess) {
                    callPhone(phoneno)

                    // 与拨号并行，检测sim卡状态
                    val telephonyManager = getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
                    val simState = telephonyManager.simState
                    if (simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN) {
                        snackbar("未检测到sim卡或当前sim卡不可用，请另行拨号$phoneno")
                    }
                }
            }
            R.id.item_media -> AHMediaList().startActivity(this)
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
            R.id.item_data_binding -> {
                //                startActivity(new Intent(this, ActivityDataBinding.class));
                val itemView = view as KNormalItemView
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair(itemView.textViewName, "text_transition_name"))
                ActivityCompat.startActivity(this, Intent(this, ActivityDataBinding::class.java), optionsCompat.toBundle())
            }
            R.id.item_flyco_dialog -> {
                val textView = TextView(this)
                textView.text = "Hello World!"
                val dialog = AlertDialog.Builder(this, R.style.Dialog_Nobackground)
                        .setView(textView).create()
                dialog.show()
            }

            R.id.item_volley -> {
                // https://developer.android.google.cn/training/volley/simple.html
                // https://github.com/google/volley
                volley("https://github.com/google/volley")
                volley("http://blog.csdn.net/linmiansheng/article/details/21646753")
            }
        }
    }

    private fun volley(http: String) {
        if (queue == null) {
            queue = Volley.newRequestQueue(this)
        }
        val request = StringRequest(Request.Method.GET, http, Response.Listener { Log.i("ULog_default", http + DateFormat.getTimeInstance().format(Date())) }, Response.ErrorListener { Log.i("ULog_default", http + DateFormat.getTimeInstance().format(Date())) })
        queue!!.add(request)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {

        var a: String? = null
        var b: Int = 0
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == 345) {
            final String path = KPath.getPath(this, data.getData());
            KLog.i(path);

            if (path.endsWith(".mp4")) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(path, new HashMap<String, String>());
                    Bitmap bmp = retriever.getFrameAtTime(1);
                    ImageView iv_test = findViewById(R.id.iv_test);
                    iv_test.setImageDrawable(new BitmapDrawable(bmp));
                } catch (Exception e) {
                    e.printStackTrace();
                    KLog.e(e);
                }
            } else
                showImage(path);
        }
    }

    protected void showImage(final String imagePath) {
        Dialog dialog = new Dialog(this) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                ImageView imageView = new ImageView(getContext());
                setContentView(imageView);
                if (imagePath.endsWith(".mp4")) {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    imageView.setImageBitmap(bitmap);
                } else
                    x.image().bind(imageView, imagePath);

                setTitle("已选图片");
            }
        };
        dialog.show();
    }*/
}
