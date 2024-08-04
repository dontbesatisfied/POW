package satisfied.be.dont.pow.core.web.security

import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import satisfied.be.dont.pow.core.crypto.Authenticator
import satisfied.be.dont.pow.core.web.security.token.AccessToken
import satisfied.be.dont.pow.core.web.security.token.BearerToken
import satisfied.be.dont.pow.core.service.IUserService

@Component
class BearerTokenAuthenticationManager: ReactiveAuthenticationManager {

    @Autowired
    private lateinit var authenticator: Authenticator

    @Autowired
    private lateinit var userService: IUserService

    // Authentication은 현재 접근하는 주체의 정보와 권한을 담는 인터페이스
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        if (authentication !is BearerToken) {
            return Mono.empty()
        }

        return mono {
            validate(authentication)
        }
    }


    suspend fun validate(bearerToken: BearerToken): Authentication? {

        val ref = authenticator.decode(bearerToken.credentials) ?: return null
        val user = userService.getUser(ref) ?: return null
        if (!user.sys.isActivated) {
            return null
        }

        return AccessToken(user)

    }
}