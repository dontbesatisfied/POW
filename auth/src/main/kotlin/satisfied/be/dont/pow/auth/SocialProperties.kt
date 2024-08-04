package satisfied.be.dont.pow.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "social")
data class SocialProperties(
    val kakao: Kakao = Kakao(),
    val naver: Naver = Naver(),
    val google: Google = Google()
) {

    data class Kakao(
        val appKey: String = "",
        val redirectUri: String = "",
        val secret: String = ""
    )



    data class Naver(
        val clientId: String = "",
        val redirectUri: String = "",
        val secret: String = ""
    )



    data class Google(
        val clientId: String = "",
        val redirectUri: String = "",
        val secret: String = ""
    )
}
