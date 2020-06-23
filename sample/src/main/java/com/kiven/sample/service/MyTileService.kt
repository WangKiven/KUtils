package com.kiven.sample.service

import android.app.AlertDialog
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi

/**
 * Created by oukobayashi on 2020/6/23.
 * TileService 允许三方app可以添加自己的快速设定，到系统的下拉设定中，方便用户快速打开关闭某些功能.
 */
@RequiresApi(Build.VERSION_CODES.N)
class MyTileService: TileService() {
    private var active = false

    /**
     * 开始监听状态变化时，由于TileService死亡等原因，可能会导致手机系统保存的状态过时。
     * MyTileService重启后，需要这个方法来更新状态。
     */
    override fun onStartListening() {
        qsTile.state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
//        qsTile.icon = Icon.createWithResource(this, R.drawable.ic_vpn)
//        qsTile.label = "Hahha"
//        qsTile.subtitle = "lalal"
        qsTile.updateTile()
    }

    override fun onClick() {
        active = !active
        onStartListening()


        val dailog = AlertDialog.Builder(this)
                .setMessage("active = $active")
                .create()

        showDialog(dailog)

//        startActivityAndCollapse()
//        startActivity()
    }
}