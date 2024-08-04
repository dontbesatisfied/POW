package satisfied.be.dont.pow.auth.controller

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/", produces = ["application/vnd.pow.oauth+json;charset=utf8"])
class RootController {

    //-----------------------------------------------------
    // Robots
    //-----------------------------------------------------
    @GetMapping("/robots.txt")
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    suspend fun robots(): String {
        return "User-agent: *\nDisallow: /"
    }



    //-----------------------------------------------------
    // Health
    //-----------------------------------------------------
    @GetMapping("/health", headers = ["X-POW-System=1"])
    @ResponseStatus(HttpStatus.OK)
    @Hidden
    fun health(exchange: ServerWebExchange) {
        exchange.response.statusCode = if(ready) HttpStatus.OK else HttpStatus.BAD_GATEWAY
    }



    companion object {
        var ready: Boolean = false
    }
}