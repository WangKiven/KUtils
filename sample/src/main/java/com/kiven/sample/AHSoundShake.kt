package com.kiven.sample

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.*
import android.os.*
import android.view.inputmethod.EditorInfo
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.callBack.CallBack
import com.kiven.kutils.callBack.Function
import com.kiven.sample.util.*
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.math.log
import kotlin.math.min
import kotlin.random.Random


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

//            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)
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

        addTitle("生成音频")

        var x = 40
        var random = Random(x)
        var audioMode = Function<Int, Number> { it % x }
        val audioModes = mapOf<String, Function<Int, Number>>(
            "it % x" to audioMode,
            "(it + (random.nextInt() * 0.15)) % x" to Function { (it + (random.nextInt(x) * 0.15).toInt()) % x },
            "(it*it) % x" to Function { (it*it) % x },
            "log(it.toFloat(), 10f) % x" to Function { log(it.toFloat(), 3f) % x },
        )

        addBtn("选择音频模式") {
            activity.showListDialog(audioModes.keys.toTypedArray()) {_,s ->
                val am = audioModes[s]
                if (am != null) audioMode = am
            }
        }
        addBtn("设置音频模式变量值") {
            activity.getInput("x值", x.toString(), EditorInfo.TYPE_CLASS_NUMBER) {
                x = it.toString().toIntOrNull() ?: 0
                if (x > 0) {
                    random = Random(x)
                }
            }
        }

        // 生成音频: https://www.yht7.com/news/173702
        // MediaDataSource: https://blog.csdn.net/weixin_31034309/article/details/114851739
        addBtn("生成音频") {
            val timeLength = 2000 //音频时长, 单位：毫秒

            val data = createFileData(ByteArray(timeLength * 32) {
                try {
                    audioMode.callBack(it).toByte()
                } catch (e: Throwable) {
                    0
                }
            })

            showTip(data.copyOfRange(44, 300).joinToString{it.toString()})

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val player = MediaPlayer()
                player.setDataSource(object : MediaDataSource() {
                    override fun close() {
                    }

                    override fun readAt(
                        position: Long,
                        buffer: ByteArray,
                        offset: Int,
                        size: Int
                    ): Int {
                        val l = min(min(size, buffer.size - offset), (data.size - position).toInt())
                        for (i in 0 until l) {
                            buffer[offset + i] = data[(position + i).toInt()]
                        }
                        showTip("读取 position=$position offset=$offset size=$size l=$l")
                        return l
                    }

                    override fun getSize(): Long {
                        return data.size.toLong()
                    }
                })
                player.setOnCompletionListener {
                    player.release()
                    showTip("播放完成")
                }
                player.prepare()
                player.start()
                showTip("播放开始")
            } else {

            }
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


    private fun buildWavHeader(dataLength: Int, srate: Int, channel: Int, format: Int): ByteArray {
        val header = ByteArray(44)
        val totalDataLen = (dataLength + 36).toLong()
        val bitrate = (srate * channel * format).toLong()
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = format.toByte()
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channel.toByte()
        header[23] = 0
        header[24] = (srate and 0xff).toByte()
        header[25] = (srate shr 8 and 0xff).toByte()
        header[26] = (srate shr 16 and 0xff).toByte()
        header[27] = (srate shr 24 and 0xff).toByte()
        header[28] = (bitrate / 8 and 0xff).toByte()
        header[29] = (bitrate / 8 shr 8 and 0xff).toByte()
        header[30] = (bitrate / 8 shr 16 and 0xff).toByte()
        header[31] = (bitrate / 8 shr 24 and 0xff).toByte()
        header[32] = (channel * format / 8).toByte()
        header[33] = 0
        header[34] = 16
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (dataLength and 0xff).toByte()
        header[41] = (dataLength shr 8 and 0xff).toByte()
        header[42] = (dataLength shr 16 and 0xff).toByte()
        header[43] = (dataLength shr 24 and 0xff).toByte()
        return header
    }

    private fun createFileData(pcmData: ByteArray): ByteArray {
        return buildWavHeader(pcmData.size, 16000, 1, 16) + pcmData
    }

    fun writeToFile(filePath: String?, pcmData: ByteArray): Boolean {
        var bos: BufferedOutputStream? = null
        try {
            bos = BufferedOutputStream(FileOutputStream(filePath))
            val header = buildWavHeader(pcmData.size, 16000, 1, 16)
            bos.write(header, 0, 44)
            bos.write(pcmData)
            bos.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }
}