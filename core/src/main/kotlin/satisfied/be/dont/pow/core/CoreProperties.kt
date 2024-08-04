package satisfied.be.dont.pow.core

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "pow")
data class CoreProperties(
    val security: SecurityProperty = SecurityProperty(),
    val callback: Callback = Callback()
) {

    data class Callback(
        val accountActivateBaseUri: String = "",
        val passwordResetBaseUri: String = ""
    )



    data class SecurityProperty(
        val accessToken: JwtProperty = JwtProperty(),
        val refreshToken: JwtProperty = JwtProperty(),
        val email: EncryptProperty = EncryptProperty(),
        val password: EncryptProperty = EncryptProperty(),
    ) {

        data class EncryptProperty(
            val salt: String = ""
        )

        data class JwtProperty(
            val secret: String = "",
            val ttlSec: String = "2592000" // 30days
        )
    }
}

