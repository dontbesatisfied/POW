package satisfied.be.dont.pow.core.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@ConditionalOnProperty("spring.data.redis.host", matchIfMissing = false)
class RedisConfig {

    // https://velog.io/@hkyo96/Spring-RedisTemplate-Serializer-%EC%84%A4%EC%A0%95
    // https://velog.io/@dev_hammy/GuideAccessing-Data-Reactively-with-Redis#starting-with-spring-initializr
    @Bean
    @ConditionalOnMissingBean(ReactiveRedisTemplate::class)
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
        val stringSerializer = StringRedisSerializer()
        val serializationContext = RedisSerializationContext.newSerializationContext<String, String>()
            .key(stringSerializer)
            .value(stringSerializer)
            .hashKey(stringSerializer)
            .hashValue(stringSerializer)
            .build()

        return ReactiveRedisTemplate(factory, serializationContext)
    }
}