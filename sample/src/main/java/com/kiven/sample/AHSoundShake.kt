package com.kiven.sample

import android.app.Activity
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.*
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.sample.util.showSnack


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
    }
}