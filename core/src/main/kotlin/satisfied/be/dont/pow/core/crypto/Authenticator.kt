package satisfied.be.dont.pow.core.crypto

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import satisfied.be.dont.pow.core.CoreProperties
import satisfied.be.dont.pow.core.exception.BadRequest
import satisfied.be.dont.pow.core.extension.Json
import satisfied.be.dont.pow.core.extension.Json.Companion.fromHashMap
import satisfied.be.dont.pow.core.extension.Json.Companion.fromJson
import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.User
import java.time.Duration

@Component
@ConditionalOnProperty(prefix = "pow.security", name = ["access-token.secret", "refresh-token.secret"])
class Authenticator {

    @Autowired
    private lateinit var coreProperties: CoreProperties

    private val accessTokener: JsonWebToken by lazy {
        JsonWebToken(coreProperties.security.accessToken.secret, Jwts.SIG.HS256, Duration.ofSeconds(coreProperties.security.accessToken.ttlSec.toLong()))
    }

    private val refreshTokener: JsonWebToken by lazy {
        JsonWebToken(coreProperties.security.refreshToken.secret, Jwts.SIG.HS256, Duration.ofSeconds(coreProperties.security.refreshToken.ttlSec.toLong()))
    }

    private val PAYLOAD_KEY = "data"



    fun issue(payload: Refer<User>): JwtPack {
        return JwtPack(
            accessTokener.issue(mapOf(PAYLOAD_KEY to payload.asMap())),
            refreshTokener.issue(mapOf(PAYLOAD_KEY to payload.asMap()))
        )
    }



    fun decode(accessToken: String): Refer<User>? {
        return try {
            fromHashMap(accessTokener.decode(accessToken).get(PAYLOAD_KEY, LinkedHashMap::class.java))
        } catch (e: JwtException) {
            null
        }
    }



    fun renew(accessToken: String, refreshToken: String): JwtPack {
        try {
            accessTokener.decode(accessToken)
            throw BadRequest("Not expired token.", details = mapOf("accessToken" to accessToken))
        } catch (e: ExpiredJwtException) {
            // normal case
        } catch (e: JwtException) {
            throw BadRequest(code = BadRequest.Codes.ValidationError, details = mapOf("accessToken" to accessToken, "error" to e.message))
        }

        val decodedRefToken: Claims
        try {
            decodedRefToken = refreshTokener.decode(refreshToken)
        } catch (e: JwtException) {
            throw BadRequest(code = BadRequest.Codes.ValidationError, details = mapOf("refreshToken" to refreshToken, "error" to e.message))
        }

        val payload = decodedRefToken[PAYLOAD_KEY] ?: throw BadRequest(code = BadRequest.Codes.MissingObject, details = mapOf("refreshToken" to decodedRefToken))

        return JwtPack(
            accessTokener.issue(mapOf(PAYLOAD_KEY to payload)),
            refreshTokener.issue(mapOf(PAYLOAD_KEY to payload), decodedRefToken.expiration)
        )
    }



    data class JwtPack(
        val accessToken: String,
        val refreshToken: String
    )
}