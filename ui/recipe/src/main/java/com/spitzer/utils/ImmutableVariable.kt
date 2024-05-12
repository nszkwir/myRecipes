package com.spitzer.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ImmutableVariable<T : Any>(
    private val onSetCallback: ((T) -> Unit)? = null
) : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw UninitializedPropertyAccessException("${property.name} has not been initialized yet")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.value == null) {
            this.value = value
            onSetCallback?.invoke(value) // Execute the callback function on the first set
        } else {
            // Don't set the value anymore
        }
    }
}