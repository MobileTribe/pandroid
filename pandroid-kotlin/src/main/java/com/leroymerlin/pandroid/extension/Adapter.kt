package com.leroymerlin.pandroid.extension

import android.support.annotation.LayoutRes
import android.view.View
import com.leroymerlin.pandroid.ui.list.recyclerview.HolderFactory
import com.leroymerlin.pandroid.ui.list.recyclerview.PandroidAdapter
import com.leroymerlin.pandroid.ui.list.recyclerview.RecyclerHolder
import kotlin.reflect.KClass


fun <T : Any> adapter(dsl: AdapterFunctionDsl<T>.() -> Unit) = AdapterFunctionDsl<T>().apply(dsl).adapter

class KotlinHolderFactory<T : Any> {
    @LayoutRes
    var layout: Int = 0
    var factory: HolderFactory<T>? = null;
    var holderClass: KClass<out RecyclerHolder<T>>? = null
    var binder: ((view: View, data: T, index: Int) -> Unit)? = null
    internal var _type: Int = 0
    var itemType: Any? = null
        set(value) {
            value?.let {
                if (value is Int) {
                    _type = value
                } else {
                    _type = value.hashCode()
                }
            }

        }

    internal fun create(): HolderFactory<T> {
        if (holderClass != null) {
            if (layout != 0) {
                factory = HolderFactory.create(layout, holderClass!!.java)
            } else {
                factory = HolderFactory.create(holderClass!!.java)
            }
        } else if (binder != null) {
            factory = HolderFactory.create(layout, binder!!)
        } else if(factory==null){
            throw IllegalStateException("Parameters is missing. See HolderFactory impl for details")
        }
        return factory!!
    }
}


open class AdapterFunctionDsl<T : Any> {

    val adapter = PandroidAdapter<T>()

    fun holder(dsl: KotlinHolderFactory<T>.() -> Unit) {
        val kotlinHolderFactory = KotlinHolderFactory<T>().apply(dsl)
        adapter.registerFactory(kotlinHolderFactory._type, kotlinHolderFactory.create())
    }

    fun itemTypes(matcher: (adapter: PandroidAdapter<T>, item: T, position: Int) -> Int) {
        adapter.setItemTypeMatcher(matcher)
    }

}
