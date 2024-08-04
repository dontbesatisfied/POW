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
import satisfied.be.dont.pow.auth.SocialProperties
import satisfied.be.dont.pow.core.exception.InternalServerError
import satisfied.be.dont.pow.core.exception.NotImplemented
import satisfied.be.dont.pow.core.model.GoogleAccountInfo
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import java.time.Duration

/**
 * https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko#httprest_1
 */
@Component
class GoogleSocialAuthManager: ISocialAuthManager {

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
        throw NotImplemented("Not supported. must call by client.")
////        val sessionKey = sessionManager.create()
//        return httpClient.get("https://accounts.google.com/o/oauth2/auth") {
//            url {
//                parameters.append("client_id", socialProperties.google.clientId)
//                parameters.append("redirect_uri", socialProperties.google.redirectUri)
//                parameters.append("response_type", "code")
//                parameters.append("scope", "openid")
////                parameters.append("state", sessionKey)
//
//            }
//        }.headers["Location"] ?: throw RuntimeException("LOCATION HEADER IS MISSING")
    }



    override suspend fun getUserInfo(token: String): SocialAccountInfo {
        val tokenResponse = httpClient.post("https://oauth2.googleapis.com/token") {
            headers {
                append("Content-type", ContentType.Application.Json)
            }
            setBody(
                mapOf(
                    "grant_type" to "authorization_code",
                    "client_id" to socialProperties.google.clientId,
                    "client_secret" to socialProperties.google.secret,
                    "redirect_uri" to socialProperties.google.redirectUri,
                    "code" to token,
                )
            )
        }.body<Map<String, Any>>()

        val userResponse = httpClient.get("https://www.googleapis.com/userinfo/v2/me") {
            headers {
                append("Authorization", "Bearer ${tokenResponse["access_token"]}")
            }
        }.body<Map<String, Any>>()

        return GoogleAccountInfo(
            userResponse["id"].toString(),
            SocialAccountInfo.SocialAccountType.GOOGLE.value,
            tokenResponse["access_token"] as String,
            tokenResponse["refresh_token"] as String
        ).also {
            sessionManager.setData(sessionManager.create(it.id), it, Duration.ofHours(1))
        }
    }



    override suspend fun getAccountInfo(session: String): SocialAccountInfo? {
        return sessionManager.getData(session, GoogleAccountInfo::class)
    }



    override suspend fun unlink(accessToken: String, refreshToken: String) {
//        httpClient.post("https://oauth2.googleapis.com/revoke") {
//            headers {
//                append("Content-type", "application/x-www-form-urlencoded")
//            }
//            parameters {
//                append()
//            }
//        }
    }
}