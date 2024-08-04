package satisfied.be.dont.pow.core.web.filter

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import satisfied.be.dont.pow.core.util.Base62
import java.util.*

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestIdFilter: WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestId = Base62.encode(UUID.randomUUID().toString().uppercase(Locale.ENGLISH).replace("-", ""))
        exchange.response.headers.add(requestIdKey, requestId)
        return chain.filter(exchange)
    }



    companion object {
        const val requestIdKey = "X-POW-RequestId"
    }
}