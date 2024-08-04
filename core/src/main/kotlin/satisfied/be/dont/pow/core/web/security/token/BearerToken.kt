package satisfied.be.dont.pow.core.web.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils

class BearerToken(
    private val token: String
): AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {

    init {
        isAuthenticated = false
    }



    // the credentials that prove the identity of the Principal
    override fun getCredentials(): String {
        return token
    }



    // The identity of the principal being authenticated. In the case of an authentication request with username and password, this would be the username.
    override fun getPrincipal(): Any {
        return Unit
    }
}