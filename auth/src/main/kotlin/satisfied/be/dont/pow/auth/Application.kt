package satisfied.be.dont.pow.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import satisfied.be.dont.pow.auth.controller.RootController
import satisfied.be.dont.pow.core.ModelMapper

@SpringBootApplication(exclude = [RedisReactiveAutoConfiguration::class])
@ComponentScan("satisfied.be.dont.pow") // 멀티 모듈 구조이므로, 선언안해주면 api 하위의 빈만 찾는다.
@ConfigurationPropertiesScan("satisfied.be.dont.pow")
@EnableConfigurationProperties
@EnableReactiveMongoRepositories
class Application {

    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        RootController.ready = true
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
