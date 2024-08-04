package satisfied.be.dont.pow.core.web.filter

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.cors.reactive.CorsUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
class CorsFilter: WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        exchange.response.headers.add("Access-Control-Max-Age", "3600")

        if (CorsUtils.isCorsRequest(exchange.request) || exchange.request.method == HttpMethod.OPTIONS) {
            exchange.response.headers.add("Access-Control-Allow-Origin", "*")
            exchange.response.headers.add("Access-Control-Allow-Methods", "*")
            exchange.response.headers.add("Access-Control-Allow-Credentials", "true")
            exchange.response.headers.add("Access-Control-Allow-Headers", "*")
            exchange.response.headers.add("Access-Control-Expose-Headers", "*")
        }

        return chain.filter(exchange)
    }

}