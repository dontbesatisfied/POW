package satisfied.be.dont.pow.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("satisfied.be.dont.pow") // 멀티 모듈 구조이므로, 선언안해주면 api 하위의 빈만 찾는다.
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
