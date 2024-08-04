package satisfied.be.dont.pow.auth.service

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import satisfied.be.dont.pow.auth.SocialProperties
import satisfied.be.dont.pow.core.exception.InternalServerError
import satisfied.be.dont.pow.core.model.NaverAccountInfo
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import java.time.Duration

/**
 * https://developers.naver.com/docs/login/api/api.md
 */
@Component
class NaverSocialAuthManager: ISocialAuthManager {

    @Autowired
    private lateinit var socialProperties: SocialProperties

    @Autowired
    private lateinit var sessionManager: ISocialAuthSessionManager

    private val httpClient = HttpClient(CIO) {
        followRedirects = false
        expectSuccess = false

        HttpResponseValidator {
            validateResponse { response ->
                if (response.status.value >= HttpStatusCode.BadRequest.value) {
                    val error: Error = response.body()
                    throw InternalServerError("Http request failed.", code = InternalServerError.Codes.ReadError, details = mapOf("status" to response.status.value, "message" to error.message))
                }
            }
        }

        install(ContentNegotiation) { jackson() }
    }


    private val asd = HttpClient(CIO) {
        install(HttpSend) {

        }
    }



    override suspend fun authorize(): String {
        return UriComponentsBuilder.fromUriString("https://nid.naver.com/oauth2.0/authorize").apply {
            queryParam("response_type", "code")
            queryParam("client_id", socialProperties.naver.clientId)
            queryParam("redirect_uri", socialProperties.naver.redirectUri)
        }.build().toUriString()
    }



    override suspend fun getUserInfo(token: String): SocialAccountInfo {
        val tokenResponse = httpClient.get("https://nid.naver.com/oauth2.0/token") {
            url {
                parameters.append("grant_type", "authorization_code")
                parameters.append("client_id", socialProperties.naver.clientId)
                parameters.append("client_secret", socialProperties.naver.secret)
                parameters.append("code", token)
            }
        }.body<Map<String, Any>>()

        val userResponse = httpClient.get("https://openapi.naver.com/v1/nid/me") {
            headers {
                append("Authorization", "Bearer ${tokenResponse["access_token"]}")
            }
        }.body<Map<String, Any>>()

        return NaverAccountInfo(
            (userResponse["response"] as Map<String, String>)["id"]!!,
            SocialAccountInfo.SocialAccountType.NAVER.value,
            tokenResponse["access_token"] as String,
            tokenResponse["refresh_token"] as String
        ).also {
            sessionManager.setData(sessionManager.create(it.id), it, Duration.ofHours(1))
        }
    }



    override suspend fun getAccountInfo(session: String): SocialAccountInfo? {
        return sessionManager.getData(session, NaverAccountInfo::class)
    }



    override suspend fun unlink(accessToken: String, refreshToken: String) {
        httpClient.get("https://nid.naver.com/oauth2.0/token") {
//            url {
//                parameters.append("grant_type", "delete")
//                parameters.append("client_id", socialProperties.naver.clientId)
//                parameters.append("client_secret", socialProperties.naver.secret)
//                parameters.append("access_token", accessToken)
//            }
            parameters {
                append("grant_type", "delete")
                append("client_id", socialProperties.naver.clientId)
                append("client_secret", socialProperties.naver.secret)
                append("access_token", accessToken)
            }
        }
    }
}