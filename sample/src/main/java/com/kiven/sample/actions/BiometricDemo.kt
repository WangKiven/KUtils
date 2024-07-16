package com.kiven.sample.actions

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.widget.TextView
import com.kiven.kutils.tools.KGranting
import com.kiven.sample.util.showDialog
import com.kiven.sample.util.showTip

/**
 * Created by oukobayashi on 2019-09-17.
 */
class BiometricDemo(val mActivity: Activity) {

    private fun showDialog(s: String) {
        mActivity.showDialog(s)
    }

    fun test() {
        // 需要配置权限：
        // <!--指纹识别，低版本需要-->
        // <uses-permission android:name="android.permission.USE_FINGERPRINT"/>
        // <!-- 生物识别，高版本需要 -->
        // <uses-permission android:name="android.permission.USE_BIOMETRIC"/>

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bm = mActivity.getSystemService(BiometricManager::class.java)
            showTip("api 29 检测生物识别功能")
            when (bm.canAuthenticate()) {
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    showDialog("用户没设置生物识别。功能存在，但是用户没打开")
                    return
                }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    showDialog("当前设备不支持或不可用生物识别")
                    return
                }
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    showDialog("可以使用生物特征识别（已注册并可用）")
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    showDialog("没有生物识别硬件")
                    return
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fm = mActivity.getSystemService(FingerprintManager::class.java)
            // 确定指纹硬件是否存在且功能正常
            if (!fm.isHardwareDetected) {
                showDialog("指纹硬件不存在或不可用")
                return
            }
            // 确定是否至少注册了一个指纹
            if (!fm.hasEnrolledFingerprints()) {
                showDialog("用户没设置指纹")
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 好像不需要在这里请求权限，在maiifest设置就够了
            KGranting.requestPermissions(mActivity, Manifest.permission.USE_BIOMETRIC, "指纹") {
                if (it) {
                    val bp = BiometricPrompt.Builder(mActivity)
                            .setTitle("指生物别测试")
                            .setDescription("是什么流程呀？")
                            .setNegativeButton("取消", mActivity.mainExecutor, DialogInterface.OnClickListener { _, i ->
                                showTip("点击取消了 $i")
                            })
                            .build()

                    val cs = CancellationSignal()
                    cs.setOnCancelListener { showTip("CancellationSignal 取消什么") }

                    val ac = object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                            super.onAuthenticationError(errorCode, errString)
                            showTip("AuthenticationCallback onAuthenticationError 设备没有开启生物识别")
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            showTip("AuthenticationCallback onAuthenticationFailed 识别失败")
                        }

                        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                            super.onAuthenticationHelp(helpCode, helpString)
                            showTip("AuthenticationCallback onAuthenticationHelp")
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                            super.onAuthenticationSucceeded(result)
                            showTip("AuthenticationCallback onAuthenticationSucceeded 识别成功")
                        }
                    }

                    bp.authenticate(cs, mActivity.mainExecutor, ac)
//                        cs.cancel()
                }
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val fm = mActivity.getSystemService(FingerprintManager::class.java)
                val cs = CancellationSignal()
                cs.setOnCancelListener { showTip("CancellationSignal 取消什么") }

                val tv = TextView(mActivity)
                tv.text = "放上手指开始识别"
                val dialog = AlertDialog.Builder(mActivity)
                        .setView(tv)
                        .setOnCancelListener {
                            cs.cancel()
                        }
                        .create()

                val ac = object : FingerprintManager.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                        // CancellationSignal.cancel() 也会到这里来
                        showTip("AuthenticationCallback onAuthenticationError $errorCode-$errString")
                        dialog.hide()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        showTip("AuthenticationCallback onAuthenticationFailed")
                        tv.text = "验证失败"
                    }

                    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                        super.onAuthenticationHelp(helpCode, helpString)
                        showTip("AuthenticationCallback onAuthenticationHelp")
                    }

                    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                        showTip("AuthenticationCallback onAuthenticationSucceeded")
                        dialog.hide()
                    }
                }

                dialog.show()
                fm.authenticate(null, cs, 0, ac, null)
//                    cs.cancel()
            }
        }
    }
}