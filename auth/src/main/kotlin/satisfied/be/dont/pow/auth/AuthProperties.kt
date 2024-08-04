package satisfied.be.dont.pow.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth")
data class AuthProperties(
    val registerUri: String = ""
) {

}
