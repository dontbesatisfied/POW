package satisfied.be.dont.pow.core.web.security

import org.springframework.security.config.Customizer
import org.springframework.security.config.web.server.ServerHttpSecurity

class AuthorizeExchanger(
    val exchangeCustomizer: Customizer<ServerHttpSecurity.AuthorizeExchangeSpec>
)