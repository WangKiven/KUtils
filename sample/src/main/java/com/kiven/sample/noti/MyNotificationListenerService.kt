package com.kiven.sample.noti

import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import com.kiven.sample.util.showTip

/**
 * Created by oukobayashi on 2019-12-11.
 * https://blog.csdn.net/xiayiye5/article/details/83688396
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MyNotificationListenerService: NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.apply {
            showTip("监听到通知：${notification.tickerText}")
        }
    }

    /*override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        super.onNotificationPosted(sbn, rankingMap)
        rankingMap?.orderedKeys?.forEach {
            val ranking = Ranking()
            rankingMap.getRanking(it, ranking)
            ranking.importanceExplanation
        }
    }*/
}