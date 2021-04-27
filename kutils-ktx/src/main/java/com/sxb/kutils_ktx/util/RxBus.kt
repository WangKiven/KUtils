package com.sxb.kutils_ktx.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.kiven.kutils.tools.KUtil
import java.util.*

/**
 * Created by kiven
 */
object RxBus {
    class PostMessage(val eventName: String, val data: Any?)
    class Register(val owner: Any, val eventName: String, val observer: Observer) {
        companion object {
            private var eventIdCount = 0

            @Synchronized
            private fun newId() = eventIdCount++
        }

        val eventId = newId()
    }

    val lifecycleObserver by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        object :LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy(lifecycleOwner: LifecycleOwner) {
                unregister(lifecycleOwner)
            }
        }
    }

    val bus by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        object :Observable() {
            override fun hasChanged(): Boolean {
                return true
            }
        }
    }
    val registers = mutableListOf<Register>()

    inline fun <reified T> register(owner: Any, eventName: String, crossinline call: (T) -> Unit): Int {
        // fragment生命周期监听，自动注销。activity的生命周期监听，通过在Application中添加监听实现，因为Activity并没继承LifecycleOwner
        if (owner is LifecycleOwner && owner !is Activity) {
            owner.lifecycle.addObserver(lifecycleObserver)
        }

        val observer = Observer { _, arg ->
            val pm = arg as PostMessage
            if (pm.eventName == eventName) {
                call(pm.data as T)
            }
        }
        sync { bus.addObserver(observer) }

        val register = Register(owner, eventName, observer)
        sync { registers.add(register) }
        return register.eventId
    }

    fun post(eventName: String, data: Any? = null) {
        sync {

        }
        bus.notifyObservers(PostMessage(eventName, data))
    }

    @Synchronized
    fun sync(call: () -> Unit) {
        call()
    }

    /**
     * eventName:String? = null 表示注销当前拥有者所有的事件
     */
    fun unregister(owner: Any, eventName: String? = null) {
        sync {
            registers.removeAll {
                if (it.owner == owner && (eventName == null || it.eventName == eventName)) {
                    bus.deleteObserver(it.observer)
                    return@removeAll true
                }

                return@removeAll false
            }
        }
    }

    /**
     * 之所以不重载，是因为owner也可能是字符串
     */
    fun unregisterByName(eventName: String) {
        sync {
            registers.removeAll {
                if (it.eventName == eventName) {
                    bus.deleteObserver(it.observer)
                    return@removeAll true
                }

                return@removeAll false
            }
        }
    }

    /**
     * 之所以不重载，是因为owner也可能是Int类型
     */
    fun unregisterById(eventId: Int) {
        sync {
            registers.removeAll {
                if (it.eventId == eventId) {
                    bus.deleteObserver(it.observer)
                    return@removeAll true
                }

                return@removeAll false
            }
        }
    }

    init {
        KUtil.getApp().registerActivityLifecycleCallbacks(object :Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                unregister(activity)
            }

        })
    }
}