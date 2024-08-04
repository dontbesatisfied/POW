package satisfied.be.dont.pow.core.annotation

import satisfied.be.dont.pow.core.model.IEntity
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
annotation class AutoDeleteBy(
    val parentModel: KClass<out IEntity>,
    val referenceField: String,
    val nullable: Boolean = false
)
