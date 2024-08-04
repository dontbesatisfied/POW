package satisfied.be.dont.pow.auth.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import satisfied.be.dont.pow.auth.SocialAuthManagerFactory
import satisfied.be.dont.pow.auth.AuthProperties
import satisfied.be.dont.pow.auth.annotation.Password
import satisfied.be.dont.pow.core.annotation.RateLimit
import satisfied.be.dont.pow.core.exception.BadRequest
import satisfied.be.dont.pow.core.exception.NotFound
import satisfied.be.dont.pow.core.exception.Unauthorized
import satisfied.be.dont.pow.core.exception.Unprocessable
import satisfied.be.dont.pow.core.extension.findEnumEntryByValue
import satisfied.be.dont.pow.core.model.Refer.Companion.asRef
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.model.UserToken
import satisfied.be.dont.pow.core.service.IUserService
import satisfied.be.dont.pow.core.service.IUserTokenService
import java.net.URI

@RestController
@RequestMapping("/auth", produces = ["application/vnd.pow.oauth+json;charset=utf8"])
@Validated
@Tag(name = "auth", description = "manage auth")
class AuthController {

    @Autowired
    private lateinit var socialAuthManagerFactory: SocialAuthManagerFactory

    @Autowired
    private lateinit var userService: IUserService

    @Autowired
    private lateinit var userTokenService: IUserTokenService

    @Autowired
    private lateinit var authProperties: AuthProperties



    @GetMapping("/{socialLogInProvider}/cb")
    @Hidden
    suspend fun getLoginCallback(
        exchange: ServerWebExchange,
        @PathVariable socialLogInProvider: String,
        @RequestParam code: String
    ): ResponseEntity<UserWithTokenVO> {

        val provider = findEnumEntryByValue<SocialAccountInfo.SocialAccountType>(socialLogInProvider) ?: throw Unprocessable("Not supported social type.", Unprocessable.Codes.NotSupported, mapOf("social" to socialLogInProvider))
        val socialAccountInfo = socialAuthManagerFactory.get(provider).getUserInfo(code)

        val user = userService.getUser(socialAccountInfo)
        if (user != null) {
            val userToken = userTokenService.updateUserToken(user.asRef()) ?: throw NotFound(details = mapOf("type" to user.sys.type, "id" to user.sys.id))

            return ResponseEntity.status(HttpStatus.OK).body(UserWithTokenVO(user, userToken.sys.accessToken, userToken.sys.refreshToken))
        }

        val registerUri = UriComponentsBuilder.fromUri(
            URI(authProperties.registerUri)
        ).apply {
            queryParam("provider", socialLogInProvider)
            queryParam("session", socialAccountInfo.id)
        }.build().toUri()

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(registerUri).build()
    }



    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @RateLimit( 300)
    suspend fun login(
        exchange: ServerWebExchange,
        @RequestBody @Valid body: LoginVO
    ): UserWithTokenVO {
        return userService.login(body.email, body.password)?.let {
            val userToken = userTokenService.updateUserToken(it.asRef()) ?: throw NotFound(details = mapOf("type" to it.sys.type, "id" to it.sys.id))

            UserWithTokenVO(it, userToken.sys.accessToken, userToken.sys.refreshToken)
        } ?: throw Unauthorized("Invalid email or password.", Unauthorized.Codes.Unauthorized)
    }



    @PostMapping("/{socialLogInProvider}/login")
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    @Operation(summary = "log in")
    suspend fun login(
        exchange: ServerWebExchange,
        @Schema(description = "social login provider", examples = ["naver", "kakao"])
        @PathVariable socialLogInProvider: String
    ) {
        val provider = findEnumEntryByValue<SocialAccountInfo.SocialAccountType>(socialLogInProvider) ?: throw Unprocessable("Not supported social type.", Unprocessable.Codes.NotSupported, mapOf("social" to socialLogInProvider))
        val redirectUri = socialAuthManagerFactory.get(provider).authorize()
        exchange.response.headers.set("Location", redirectUri)
    }



    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "sign up")
    suspend fun register(
        exchange: ServerWebExchange,
        @RequestBody @Valid body: CreateUserVO
    ): User {
        return userService.createUser(body.name, body.email, body.password).also {
            userTokenService.createUserToken(it.asRef())
        }
    }



    @PostMapping("/{socialLogInProvider}/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "sign up through social", description = "must call this api after call login API.")
    suspend fun register(
        exchange: ServerWebExchange,
        @Schema(description = "social login provider", examples = ["naver", "kakao"])
        @PathVariable socialLogInProvider: String,
        @RequestBody @Valid body: CreateSocialUserVO
    ): UserWithTokenVO {

        val provider = findEnumEntryByValue<SocialAccountInfo.SocialAccountType>(socialLogInProvider) ?: throw Unprocessable("Not supported social type.", Unprocessable.Codes.NotSupported, mapOf("social" to socialLogInProvider))

        return socialAuthManagerFactory.get(provider).getAccountInfo(body.session)?.let {
            val user = userService.createUser(body.name, it)
            val userToken = userTokenService.createUserToken(user.asRef())

            UserWithTokenVO(user, userToken.sys.accessToken, userToken.sys.refreshToken)
        } ?: throw BadRequest(code = BadRequest.Codes.InvalidValue, details = mapOf("session" to body.session))
    }



    @PutMapping("/renew")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "renew access token.", description = "must use expired accessToken.")
    suspend fun renew(
        exchange: ServerWebExchange,
        @RequestBody @Valid body: RenewTokenVO
    ): UserToken {
        return userTokenService.updateUserToken(body.accessToken, body.refreshToken) ?: throw NotFound(message = "Token's owner could not be found.")
    }



    data class RenewTokenVO(
        @field:Schema(description = "expired access token")
        val accessToken: String,
        @field:Schema(description = "refresh token")
        val refreshToken: String
    )



    data class UserWithTokenVO(
        @field:Schema(description = "user data")
        val user: User,
        @field:Schema(description = "new access token")
        val accessToken: String,
        @field:Schema(description = "new refresh token")
        val refreshToken: String
    )



    data class LoginVO(
        @field:Email
        val email: String,
        val password: String
    )



    data class CreateUserVO(
        @field:Length(min = 1, max = 32)
        @field:Schema(description = "name to use in service", minLength = 1, maxLength = 32)
        val name: String,
        @field:Password
        val password: String,
        @field:Email
        val email: String
    )



     data class CreateSocialUserVO(
        @field:Length(min = 1, max = 32)
        @field:Schema(description = "name to use in service", minLength = 1, maxLength = 32)
        val name: String,
        @field:Schema(description = "session key in url query params")
        val session: String
    )

}