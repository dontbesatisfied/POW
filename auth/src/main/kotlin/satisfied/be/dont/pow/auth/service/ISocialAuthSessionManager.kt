package satisfied.be.dont.pow.auth.service

import satisfied.be.dont.pow.core.model.SocialAccountInfo
import java.time.Duration
import kotlin.reflect.KClass

interface ISocialAuthSessionManager {
    suspend fun create(key: String?): String
    suspend fun validate(key: String): Boolean
    suspend fun setData(key: String, data: SocialAccountInfo, ttl: Duration)
    suspend fun <T: SocialAccountInfo> getData(key: String, clazz: KClass<T>): T?
}