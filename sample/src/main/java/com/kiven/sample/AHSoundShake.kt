package com.kiven.sample

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.*
import android.os.*
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.util.showDialog
import com.kiven.sample.util.showSnack
import com.kiven.sample.util.showTip
import com.kiven.sample.util.showToast


class AHSoundShake: BaseFlexActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        var hasVibrator = false
        val startVibrator = fun (vibrator: Vibrator) {

            if (hasVibrator) {
                vibrator.cancel()
                hasVibrator = false
                return
            }
            hasVibrator = true


            val pattern = longArrayOf(2000, 2000, 1000, 500)
            val amplitudes = intArrayOf(0, 100, 0, 230)// 音量，猜测：pattern和amplitudes对应，暂停阶段不播放，所有音量设置为0
            val repeat = -1
            // https://blog.csdn.net/weixin_38663354/article/details/106817126
            // todo 在这里没发现 AudioAttributes 有什么作用，可以不用的
            val aab = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
//                .setAllowedCapturePolicy()//这个属性用来设置当前音频是否允许被其他应用捕获
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
//                .setHapticChannelsMuted(true)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, repeat), aab)
//                vibrator.vibrate(VibrationEffect.createOneShot(1000, 200))
            } else {
                vibrator.vibrate(pattern, repeat, aab)
//                vibrator.vibrate(1000)
            }

            activity.showSnack("${pattern[0]}毫秒后开始震动, 能控制音量吗：${Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator.hasAmplitudeControl()}")

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrator = activity.getSystemService(Activity.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            addBtn("震动📳") { startVibrator(vibrator.defaultVibrator) }
        } else {
            val vibrator = activity.getSystemService(Activity.VIBRATOR_SERVICE) as Vibrator
            addBtn("震动📳") { startVibrator(vibrator) }
        }


        val mediaPlayer = MediaPlayer()
        // TYPE_RINGTONE 电话铃声，TYPE_NOTIFICATION 通知，TYPE_ALARM 闹铃，TYPE_ALL
        mediaPlayer.setDataSource(activity, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
        mediaPlayer.prepare()

        var isRingStart = false
        addBtn("响铃🔔") {
            if (isRingStart) {
                mediaPlayer.pause()
                isRingStart = false
            } else {
                mediaPlayer.start()
                isRingStart = true
            }
        }

        addBtn("提示音") {
            RingtoneManager.getRingtone(activity, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .play()
        }

        addBtn("录音") {
            AHRecorderPlay().startActivity(activity)
        }

        addBtn("音量+") {
            val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // 指定调节音乐的音频，增大音量，而且显示音量图形示意
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)

            showToast("音量 ${audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)}")
        }
        addBtn("音量-") {
            val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // 指定调节音乐的音频，降低音量，只有声音,不显示图形条
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND)

            showToast("音量 ${audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)}")
        }

        var isMute = false
        addBtn("静音设置") {
            // https://www.runoob.com/w3cnote/android-tutorial-audiomanager.html
            val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                audioManager.isStreamMute(AudioManager.STREAM_MUSIC) // 是否是静音, 低系统好像没有api，不知道可不可以通过音量来判断
//            }

            isMute = !isMute

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, if (isMute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE,
                                AudioManager.FLAG_SHOW_UI)
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, isMute)
            }

            showToast("静音：$isMute, 音量 ${audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)}")
        }

        addTitle("硬件")
        addBtn("查看耳机等输出设备信息") {
//            https://www.runoob.com/w3cnote/android-tutorial-audiomanager.html

            if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT)) {
                activity.showDialog("没有音频输出功能")
                return@addBtn
            }

            val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val deviceInfo = audioManager.communicationDevice
                    showTip("在用 设备${deviceInfo?.id} ${deviceInfo?.productName}")
                }

                val outputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                for (outputDevice in outputDevices) {
                    val type = when(outputDevice.type) {
                        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> "耳机扬声器（不代表使用的耳机）"
                        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "内置扬声器系统（即单声道扬声器或立体声扬声器）"
                        AudioDeviceInfo.TYPE_TELEPHONY -> "电话网络传输音频"
                        AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "用于电话的蓝牙设备"
                        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "支持A2DP配置文件的蓝牙设备"
                        AudioDeviceInfo.TYPE_BLE_HEADSET -> "TYPE_BLE_HEADSET"
                        AudioDeviceInfo.TYPE_USB_HEADSET -> "TYPE_USB_HEADSET"
                        AudioDeviceInfo.TYPE_WIRED_HEADSET -> "TYPE_WIRED_HEADSET"
                        AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "TYPE_WIRED_HEADPHONES"
                        else -> "其他 ${outputDevice.type}"
                    }
                    showTip("设备${outputDevice.id} ${outputDevice.productName} $type isSink=${outputDevice.isSink} isSource=${outputDevice.isSource}")
                }
            }

            // isSpeakerphoneOn: 检查扬声器是否打开或关闭。isBluetoothScoOn: 检查通信是否使用蓝牙SCO。
            showTip("isSpeakerphoneOn(扩音器？) = ${audioManager.isSpeakerphoneOn}, isBluetoothScoOn = ${audioManager.isBluetoothScoOn}")
            showTip("isWiredHeadsetOn（有线耳机？） = ${audioManager.isWiredHeadsetOn}, isBluetoothA2dpOn（蓝牙耳机） = ${audioManager.isBluetoothA2dpOn}")

            val mode = when (audioManager.mode) {
                AudioManager.MODE_NORMAL -> "MODE_NORMAL(普通)"
                AudioManager.MODE_RINGTONE -> "MODE_RINGTONE(铃声)"
                AudioManager.MODE_IN_CALL -> "MODE_IN_CALL(打电话)"
                AudioManager.MODE_IN_COMMUNICATION -> "MODE_IN_COMMUNICATION(通话)"
                AudioManager.MODE_CALL_SCREENING -> "MODE_CALL_SCREENING"
                else -> "其他"
            }
            showTip("mode = $mode-${audioManager.mode}")

            val ringerMode = when(audioManager.ringerMode) {
                AudioManager.RINGER_MODE_NORMAL -> "RINGER_MODE_NORMAL（普通）"
                AudioManager.RINGER_MODE_SILENT -> "RINGER_MODE_SILENT（静音）"
                AudioManager.RINGER_MODE_VIBRATE -> "RINGER_MODE_VIBRATE（震动）"
                else -> "其他"
            }
            showTip("ringerMode = $ringerMode-${audioManager.ringerMode}")
        }

        addBtn("设备插拔监听") {
            // https://blog.csdn.net/sz_chrome/article/details/107407734
            showToast("没做")
        }
    }
}