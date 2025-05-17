package deplatformed

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ServiceImpl(
    vararg val value: KClass<*>
)
