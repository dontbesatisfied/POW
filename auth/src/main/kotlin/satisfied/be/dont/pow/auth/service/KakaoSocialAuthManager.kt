package satisfied.be.dont.pow.auth.service

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import satisfied.be.dont.pow.auth.SocialProperties
import satisfied.be.dont.pow.core.exception.InternalServerError
import satisfied.be.dont.pow.core.model.KakaoAccountInfo
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import java.time.Duration

/**
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
 */
@Component
class KakaoSocialAuthManager: ISocialAuthManager {

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



    override suspend fun authorize(): String {
        return httpClient.get("https://kauth.kakao.com/oauth/authorize") {
            url {
                parameters.append("client_id", socialProperties.kakao.appKey)
                parameters.append("redirect_uri", socialProperties.kakao.redirectUri)
                parameters.append("response_type", "code")
            }
        }.headers["Location"] ?: throw InternalServerError(code = InternalServerError.Codes.MissingData, details = "Location header is missing.")
    }



    override suspend fun getUserInfo(token: String): SocialAccountInfo {
        val tokenResponse = httpClient.post("https://kauth.kakao.com/oauth/token") {
            headers {
                append("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
            }
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("grant_type", "authorization_code")
                        append("client_id", socialProperties.kakao.appKey)
                        append("redirect_uri", socialProperties.kakao.redirectUri)
                        append("code", token)
                        append("client_secret", socialProperties.kakao.secret)
                    }
                )
            )
        }.body<Map<String, Any>>()

        val userResponse = httpClient.get("https://kapi.kakao.com/v2/user/me") {
            headers {
                append("Authorization", "Bearer ${tokenResponse["access_token"]}")
                append("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
            }
        }.body<Map<String, Any>>()

        return KakaoAccountInfo(
            userResponse["id"].toString(),
            SocialAccountInfo.SocialAccountType.KAKAO.value,
            tokenResponse["access_token"] as String,
            tokenResponse["refresh_token"] as String
        ).also {
            sessionManager.setData(sessionManager.create(it.id), it, Duration.ofHours(1))
        }
    }



    override suspend fun getAccountInfo(session: String): SocialAccountInfo? {
        return sessionManager.getData(session, KakaoAccountInfo::class)
    }



    override suspend fun unlink(accessToken: String, refreshToken: String) {
        httpClient.post("https://kapi.kakao.com/v1/user/unlink") {
            headers {
                append("Authorization", "Bearer $accessToken")
            }
        }
    }
}