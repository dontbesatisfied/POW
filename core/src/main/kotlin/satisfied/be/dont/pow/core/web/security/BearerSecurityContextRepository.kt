package satisfied.be.dont.pow.core.web.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import satisfied.be.dont.pow.core.exception.NotImplemented
import satisfied.be.dont.pow.core.web.security.token.BearerToken

@Component
class BearerSecurityContextRepository: ServerSecurityContextRepository {

    @Autowired
    private lateinit var authenticationManager: ReactiveAuthenticationManager



    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        throw NotImplemented("Not supported. JWT is stateless.")
    }



    override fun load(exchange: ServerWebExchange?): Mono<SecurityContext> {
        val token = exchange?.request?.headers?.getFirst("Authorization")?.let {
            if (!it.startsWith("Bearer")) {
                return Mono.empty()
            }

            it.substring("Bearer ".length)
        } ?: return Mono.empty()

        return authenticationManager.authenticate(BearerToken(token)).map {
            SecurityContextImpl(it)
        }

    }
}