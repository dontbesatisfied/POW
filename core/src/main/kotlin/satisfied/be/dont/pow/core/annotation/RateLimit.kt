package satisfied.be.dont.pow.core.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimit(
    val maxAttempts: Int,
    val windowSec: Long = 60,
)
