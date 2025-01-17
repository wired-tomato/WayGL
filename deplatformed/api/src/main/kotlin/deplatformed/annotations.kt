package deplatformed

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ServiceImpl(
    val value: Array<KClass<*>>
)
