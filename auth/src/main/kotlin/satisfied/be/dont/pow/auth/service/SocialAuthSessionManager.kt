package satisfied.be.dont.pow.auth.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.getAndAwait
import org.springframework.data.redis.core.setAndAwait
import org.springframework.stereotype.Component
import satisfied.be.dont.pow.core.exception.InternalServerError
import satisfied.be.dont.pow.core.extension.Json.Companion.fromJson
import satisfied.be.dont.pow.core.extension.Json.Companion.toJson
import satisfied.be.dont.pow.core.model.Identity
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import java.time.Duration
import kotlin.reflect.KClass

@Component
class SocialAuthSessionManager: ISocialAuthSessionManager {

    @Autowired
    private lateinit var redis: ReactiveRedisTemplate<String, String>



    override suspend fun create(key: String?): String {
        val sessionKey = key ?: Identity.generateUid()
        if (!(redis.opsForValue().setAndAwait(sessionKey, sessionKey))) {
            throw InternalServerError(code = InternalServerError.Codes.WriteError, details = mapOf("sessionKey" to sessionKey))
        }

        return sessionKey

    }



    override suspend fun validate(key: String): Boolean {
        return !redis.opsForValue().getAndAwait(key).isNullOrEmpty()
    }



    override suspend fun setData(key: String, data: SocialAccountInfo, ttl: Duration) {
        redis.opsForValue().getAndAwait(key)?.let {
            redis.opsForValue().setAndAwait(key, data.toJson(noIgnore = true), ttl)
        } ?: throw InternalServerError(code = InternalServerError.Codes.WriteError, details = mapOf("key" to key, "data" to data.toJson()))
    }



    override suspend fun <T : SocialAccountInfo> getData(key: String, clazz: KClass<T>): T? {
        return redis.opsForValue().getAndAwait(key)?.let {
            clazz.fromJson(it)
        }
    }
}