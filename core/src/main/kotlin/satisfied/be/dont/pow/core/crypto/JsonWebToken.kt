package satisfied.be.dont.pow.core.crypto

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.MacAlgorithm
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

open class JsonWebToken (
    key: String,
    private val algorithm: MacAlgorithm,
    private val ttl: Duration
) {
    private var secretKey: SecretKey

    init {
        val byteSecret = key.toByteArray()
        val byteKey = ByteArray(algorithm.keyBitLength) {
            try {
                byteSecret[it]
            } catch (e: Throwable) {
                45 // '-'
            }
        }
        secretKey = Keys.hmacShaKeyFor(byteKey)
    }



    fun issue(payload: Map<String, *>, expiration: Date? = null): String {

        return Jwts.builder().apply {
            signWith(secretKey, algorithm)
            claims(payload)
            expiration(expiration ?: Date(System.currentTimeMillis() + ttl.toMillis()))
        }.compact()
    }



    fun decode(jwt: String): Claims {

        return Jwts.parser().apply {
            verifyWith(secretKey)
        }.build().parseSignedClaims(jwt).payload
    }



//    fun verify(jwt: String): Boolean {
//
//        return Jwts.parser().apply {
//            verifyWith(secretKey)
//        }.build().isSigned(jwt)
//    }
}