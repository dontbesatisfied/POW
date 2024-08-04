package satisfied.be.dont.pow.core.annotation.aspect

import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.getAndAwait
import org.springframework.data.redis.core.incrementAndAwait
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import satisfied.be.dont.pow.core.annotation.RateLimit
import satisfied.be.dont.pow.core.exception.TooManyRequest
import kotlin.coroutines.suspendCoroutine

@Aspect
@Component
@ConditionalOnProperty("spring.data.redis.host", matchIfMissing = false)
class RateLimitAspect {

    @Autowired
    private lateinit var redis: ReactiveRedisTemplate<String, String>

    private val luaScript = DefaultRedisScript(
    """
        local current
        current = redis.call("INCR", KEYS[1])
        if current == 1 then
            redis.call("EXPIRE", KEYS[1], ARGV[1])
        end
        return current
        """,
        Long::class.java
    )

    /**
     * 포인트컷의 지시자 종류
     * execution : 메소드 실행 조인 포인트를 매칭한다. 스프링 AOP에서 가장 많이 사용하고, 기능도 복잡하다.
     * within : 특정 타입 내의 조인 포인트를 매칭한다.
     * args : 인자가 주어진 타입의 인스턴스인 조인 포인트
     * this : 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
     * target : Target 객체(스프링 AOP 프록시가 가르키는 실제 대상)를 대상으로 하는 조인 포인트
     * @target : 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
     * @within :  주어진 애노테이션이 있는 타입 내에 있는 클래스의 메서드들에 조인포인트를 매칭
     * @annotation : "메서드"가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
     * @args : 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
     * bean : 스프링 전용 포인트컷 지시자, 빈의 이름으로 포인트컷을 지정한다
     *
     * ex. execution(* example.aop.test.*.*(..))
     * 우측부터 *의 의미는, 반환타입 / 패키지의 클래스 / 패키지의 메소드 / 파라미터 타입
     *
     * 파라미터 매칭 규칙
     * (String) : 메서드의 파라미터가 정확하게 String 타입의 파라미터이어야 포인트컷 대상
     * () : 메서드의 파라미터가 없어야 포인트컷 대상
     * (*) : 메서드의 파라미터 타입은 모든 타입을 허용하지만, 정확히 하나의 파라미터를 가진 메서드가 포인트컷 대상
     * (*, *) : 메서드의 파라미터 타입은 모든 타입을 허용하지만, 정확히 두 개의 파라미터를 가진 메서드가 포인트컷 대상
     * (..) : 메서드의 파라미터 수와 무관하게 모든 파라미터, 모든 타입을 허용한다. ( 파라미터가 없어도 된다. )
     * (String, ..) : 메서드의 첫 번째 파라미터는 String 타입으로 시작해야 하고, 나머지 파라미터 수와 무관하게 모든 파라미터, 모든 타입을 허용한다. ( Ex:// (String) , (String, xxx) , (String, xxx, xxx) 허용 )
     */
    // NOTE: spring 6.1.0 이후로 코루틴 suspend 함수 지원하므로 별도의 절차없이 로직수행 가능 (https://nomoresanta.tistory.com/2)
    @Around("@annotation(satisfied.be.dont.pow.core.annotation.RateLimit)")
    fun rateLimit(joinPoint: ProceedingJoinPoint): Any {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = method.getAnnotation(RateLimit::class.java)

        val exchange = findServerWebExchange(joinPoint.args) ?: return joinPoint.proceed()
        val ip = exchange.request.remoteAddress?.address?.hostAddress ?: return joinPoint.proceed()

        return mono {
            val attempts = (redis.opsForValue().getAndAwait(ip) ?: "0").toInt()
            if (attempts >= annotation.maxAttempts) {
                throw TooManyRequest(details = mapOf("attempts" to attempts))
            }

            (joinPoint.proceed() as Mono<*>).awaitSingleOrNull().also {
                redis.execute(luaScript, listOf(ip), listOf(annotation.windowSec.toString())).blockFirst()
            }
        }
    }



    private fun findServerWebExchange(args: Array<Any>): ServerWebExchange? {
        for (arg in args) {
            if (arg is ServerWebExchange) {
                return arg
            }
        }
        return null
    }
}