package satisfied.be.dont.pow.core.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import reactor.core.publisher.Mono
import satisfied.be.dont.pow.core.exception.Forbidden
import satisfied.be.dont.pow.core.exception.Unauthorized
import satisfied.be.dont.pow.core.extension.Json.Companion.toJson
import satisfied.be.dont.pow.core.web.security.AuthorizeExchanger
import satisfied.be.dont.pow.core.web.security.permission.Authority
import satisfied.be.dont.pow.core.web.ErrorResponse
import satisfied.be.dont.pow.core.web.filter.RequestIdFilter

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        securityContextRepository: ServerSecurityContextRepository,
        reactiveAuthenticationManager: ReactiveAuthenticationManager,
        authorizeExchanger: AuthorizeExchanger
    ): SecurityWebFilterChain {
        return http.exceptionHandling {
            it.authenticationEntryPoint { exchange, denied ->
                val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
                val error = Unauthorized(denied.message ?: "The access token is invalid.", Unauthorized.Codes.Unauthorized)
                val errorRes = ErrorResponse(requestId, error)
                val messageAsBytes = errorRes.toJson().toByteArray()
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                exchange.response.headers.contentType = org.springframework.http.MediaType.APPLICATION_JSON
                exchange.response.writeWith(Mono.just(exchange.response.bufferFactory().wrap(messageAsBytes))).apply {
//                    logger.error(errorRes, error, exchange, HttpStatus.UNAUTHORIZED.value())
                }
            }
            it.accessDeniedHandler { exchange, denied ->
                val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
                val error = Forbidden(denied.message ?: "The access token has no rights.", Forbidden.Codes.Forbidden)
                val errorRes = ErrorResponse(requestId, error)
                val messageAsBytes = errorRes.toJson().toByteArray()
                exchange.response.statusCode = HttpStatus.FORBIDDEN
                exchange.response.headers.contentType = org.springframework.http.MediaType.APPLICATION_JSON
                exchange.response.writeWith(Mono.just(exchange.response.bufferFactory().wrap(messageAsBytes))).apply {
//                    logger.error(errorRes, error, exchange, HttpStatus.FORBIDDEN.value())
                }
            }
        }
        .csrf{
            it.disable()
        }
        .formLogin{
            it.disable()
        }
        .httpBasic {
            it.disable()
        }
        .authenticationManager(reactiveAuthenticationManager)
        .securityContextRepository(securityContextRepository)
        .authorizeExchange(authorizeExchanger.exchangeCustomizer)
        .build()
    }



    @Bean
    @ConditionalOnMissingBean(AuthorizeExchanger::class)
    fun customizer(): AuthorizeExchanger {
        val customizer = Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> {
            it.anyExchange().permitAll()
        }
        return AuthorizeExchanger(customizer)
    }



    @Bean
    @ConditionalOnMissingBean(DefaultMethodSecurityExpressionHandler::class)
    fun methodSecurityExpressionHandler(): DefaultMethodSecurityExpressionHandler {
        return DefaultMethodSecurityExpressionHandler().apply {
            setRoleHierarchy(RoleHierarchyImpl.fromHierarchy(Authority.HIERARCHY))
        }
    }
}