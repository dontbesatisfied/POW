package satisfied.be.dont.pow.auth.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import satisfied.be.dont.pow.auth.annotation.validator.PasswordConstraintValidator
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PasswordConstraintValidator::class])
annotation class Password(
    val message: String = "must be a well-formed password",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)