package com.kiven.sample.arcore

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.kiven.sample.R
import com.kiven.sample.util.showSnack

/**
 * https://developers.google.cn/ar/develop/java/enable-arcore
 */
class AHARCore : KActivityHelper() {
    var supportARCoreTextView: TextView? = null
    var isSupport = -1

    val arFragment = ArFragment()
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)

        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START
        flexboxLayout.fitsSystemWindows = true



        var modelRenderable:ModelRenderable? = null
        ModelRenderable.builder()
//                .setSource(activity, Uri.parse("sceneform_face_mesh.sfb"))R.raw.sceneform_face_mesh
                .setSource(activity, R.raw.sceneform_face_mesh)
                .build()
                .thenAccept({renderable ->  modelRenderable = renderable})
                .exceptionally {
                    KLog.e(it)
                    return@exceptionally null
                }
        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            if (modelRenderable == null) return@setOnTapArPlaneListener

            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)

            val mode = TransformableNode(arFragment.transformationSystem)
                    .apply {
                        setParent(anchorNode)
                        renderable = modelRenderable
                        select()
                    }
        }

        setContentView(FrameLayout(activity).apply {
            id = R.id.ll_root

            activity.supportFragmentManager.beginTransaction().add(R.id.ll_root, arFragment, null).commit()
            addView(NestedScrollView(activity).apply {
                addView(flexboxLayout)
            })
        })

        val addTitle = fun(text: String): TextView {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
            return tv
        }

        val addView = fun(text: String, click: View.OnClickListener): Button {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
            return btn
        }

        addTitle("ARCore测试，国内需要先在应用商城下载ARCore，否则显示不支持")

        //supportARCoreBtn = addView("点我检测", View.OnClickListener { checkARCore() })
        supportARCoreTextView = addTitle("ARCore检测中")
        checkARCore()

        var session:Session? = null
        addView("go", View.OnClickListener {
            if (session != null) return@OnClickListener

            KGranting.requestPermissions(activity, Manifest.permission.CAMERA) {
                if (it) {
                    try {
                        when(ArCoreApk.getInstance().requestInstall(activity, true)) {
                            ArCoreApk.InstallStatus.INSTALLED -> {
                                session = Session(activity)

                                /*MaterialFactory.makeOpaqueWithColor(activity, Color(android.graphics.Color.RED))
                                        .thenAccept {
                                            val redSphereRenderable = ShapeFactory.makeSphere(0.1f, Vector3(0.0f, 0.15f, 0.0f), it)
                                        }*/
                            }
                            ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                                //如果 requestInstall() 返回 INSTALL_REQUESTED，则当前 Activity 将暂停，并提示用户安装或更新 ARCore：
                            }
                            else -> {}
                        }
                    }catch (e: UnavailableUserDeclinedInstallationException) {
                        activity.showSnack("不可用异常")
                    }catch (e:Throwable) {
                        activity.showSnack("不明异常 ：${e.message}")
                        e.printStackTrace()
                    }

                }
            }
        })


    }

    private fun checkARCore() {
        val availability = ArCoreApk.getInstance().checkAvailability(mActivity)
        if (availability.isTransient) {
            Handler().postDelayed(Runnable {
                checkARCore()
            }, 200)
        }

        if (availability.isSupported) {
            supportARCoreTextView?.text = "检测结果：支持ARCore ${availability.isTransient}"
            isSupport = 1
        } else {
            supportARCoreTextView?.text = "检测结果：不支持ARCore ${availability.isTransient}"
            isSupport = 0
        }
    }
}