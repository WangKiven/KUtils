package com.kiven.sample.cutImage

import android.app.Service
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kiven.kutils.callBack.CallBack
import com.kiven.kutils.callBack.Consumer
import com.kiven.sample.floatView.FloatView

class CutScreenService : Service() {
    companion object {
        private var initCall: Consumer<Service>? = null
        private var onClick: CallBack? = null
        private var text: String = "帅"
        fun putUI(t: String, l: CallBack?, i: Consumer<Service>?) {
            initCall = i
            onClick = l
            val tx = t.trim()
            if (tx.isNotBlank()){
                text = tx.substring(0, 1)
            }
        }
    }


    var floatView: FloatView? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        initCall?.callBack(this)


        if (floatView == null) {
            floatView = FloatView(
                baseContext,
                application.getSystemService(WINDOW_SERVICE) as WindowManager,
                text,
                true
            ) {
                onClick?.callBack()
            }
        }

        floatView?.showFloat()
    }

    override fun onDestroy() {
        super.onDestroy()

        floatView?.hideFloat()
        floatView = null
    }
}