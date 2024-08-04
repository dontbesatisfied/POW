package satisfied.be.dont.pow.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.web.server.ServerHttpSecurity
import satisfied.be.dont.pow.core.web.security.AuthorizeExchanger

@Configuration(value = "authSecurityConfig")
class SecurityConfig {


    @Bean
    fun customizer(): AuthorizeExchanger {
        val customizer = Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> {
            it.pathMatchers(HttpMethod.OPTIONS).permitAll()
            // Swagger
            .pathMatchers("/webjars/**").permitAll()
            .pathMatchers("/swagger/**").permitAll()
            // API
            .pathMatchers("/health").permitAll()
            .pathMatchers("/robots.txt").permitAll()
            .pathMatchers("/auth/**").permitAll()
            .pathMatchers("/accounts/*/activate").permitAll()
            .anyExchange().authenticated()
        }
        return AuthorizeExchanger(customizer)
    }
}