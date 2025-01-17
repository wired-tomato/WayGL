package net.wiredtomato.waygl.service

import java.util.Optional
import java.util.ServiceLoader
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

object Services {
    fun <T : Any> getServiceLoader(klass: KClass<T>): ServiceLoader<T> {
        return ServiceLoader.load(klass.java)
    }

    inline fun <T : Any> getService(klass: KClass<T>, crossinline pick: ServiceLoader<T>.() -> Optional<T> = { findFirst() }): T {
        val loader = getServiceLoader(klass)
        val service = loader.pick()
        return service.getOrNull() ?: throw IllegalStateException("Failed to load service: $klass")
    }

    inline fun <reified T : Any> getService(crossinline pick: ServiceLoader<T>.() -> Optional<T> = { findFirst() }): T {
        return getService(T::class, pick)
    }
}
