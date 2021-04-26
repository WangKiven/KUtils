package com.kiven.sample.util

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Created by zhangyujiu on 2017/11/27 0027 16:39
 */
object RxBus {
    class PostMessage(val eventName: String, val data: Any?)
    class Register(val owner: Any, val eventName: String, val disposable: Disposable) {
        companion object {
            private var eventIdCount = 0
            private fun newId() = eventIdCount++
        }

        val eventId = newId()
    }

    val bus by lazy {
        PublishSubject.create<PostMessage>()
    }
    val registers = mutableListOf<Register>()

    inline fun <reified T> register(owner: Any, eventName: String, crossinline call: (T) -> Unit): Int {
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
        registers.add(register)

        return register.eventId
    }

    fun post(eventName: String, data: Any? = null) {
        bus.onNext(PostMessage(eventName, data))
    }

    /**
     * eventName:String? = null 表示注销当前拥有者所有的事件
     */
    @Synchronized
    private fun sync(call: () -> Unit) {
        call()
    }

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
}