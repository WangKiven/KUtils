package com.kiven.sample.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.kiven.kutils.tools.KUtil
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Created by kiven
 */
object RxBus {
    class PostMessage(val eventName: String, val data: Any?)
    class Register(val owner: Any, val eventName: String, val disposable: Disposable) {
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
        PublishSubject.create<PostMessage>()
    }
    val registers = mutableListOf<Register>()

    inline fun <reified T> register(owner: Any, eventName: String, crossinline call: (T) -> Unit): Int {
        // fragment生命周期监听，自动注销。activity的生命周期监听，通过在Application中添加监听实现，因为Activity并没继承LifecycleOwner
        if (owner is LifecycleOwner && owner !is Activity) {
            owner.lifecycle.addObserver(lifecycleObserver)
        }

        val observable = bus.ofType(PostMessage::class.java)
                // 这里加了异常重新订阅
                .retryWhen {
                    it.flatMap {
                        Observable.timer(50,
                                TimeUnit.MILLISECONDS)
                    }
                }
        val disposable = observable.subscribe {
            if (it.eventName == eventName) {
                call(it.data as T)
            }
        }

        val register = Register(owner, eventName, disposable)
        sync { registers.add(register) }
        return register.eventId
    }

    fun post(eventName: String, data: Any? = null) {
        bus.onNext(PostMessage(eventName, data))
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
                    it.disposable.dispose()
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
                    it.disposable.dispose()
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
                    it.disposable.dispose()
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