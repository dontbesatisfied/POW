package satisfied.be.dont.pow.auth.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Email
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import satisfied.be.dont.pow.auth.SocialAuthManagerFactory
import satisfied.be.dont.pow.core.exception.InternalServerError
import satisfied.be.dont.pow.core.exception.NotFound
import satisfied.be.dont.pow.core.exception.Unprocessable
import satisfied.be.dont.pow.core.extension.findEnumEntryByValue
import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.Refer.Companion.asRef
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.web.security.token.AccessToken
import satisfied.be.dont.pow.core.service.IUserService

@RestController
@RequestMapping("/accounts", produces = ["application/vnd.pow.oauth+json;charset=utf8"])
@Validated
@SecurityRequirement(name = "Bearer")
@Tag(name = "account", description = "manage account")
class AccountController {

    @Autowired
    private lateinit var userService: IUserService

    @Autowired
    private lateinit var socialAuthManagerFactory: SocialAuthManagerFactory


    @GetMapping("/{accountId}/activate")
    @ResponseStatus(HttpStatus.PERMANENT_REDIRECT)
    @Hidden
    suspend fun activate(
        exchange: ServerWebExchange,
        @PathVariable accountId: String,
        @RequestParam token: String
    ) {
        val user = Refer.of<User>(accountId)
        userService.activateUser(user, token) ?: throw NotFound(code = NotFound.Codes.ResourceNotFound, details = mapOf("type" to user.sys.targetType, "id" to user.sys.id))
        // TODO: uri 변경해야함
        exchange.response.headers.set("Location", "http://localhost:8080/v1")
    }



    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(hasRole('USER') and #accountId == principal.sys.id) or hasRole('ADMIN')")
    @Operation(summary = "get user")
    suspend fun get(
        exchange: ServerWebExchange,
        auth: AccessToken,
        @PathVariable accountId: String
    ): User? {
        val user = auth.principal.asRef()
        return userService.getUser(user)
            ?: throw NotFound(code = NotFound.Codes.ResourceNotFound, details = mapOf("type" to user.sys.targetType, "id" to user.sys.id))
    }



//    @PutMapping
//    @ResponseStatus(HttpStatus.OK)
//    @Hidden
//    suspend fun update(
//        exchange: ServerWebExchange,
//        @RequestBody @Valid body: UpdateUserVO
//    ): User? {
//        val user = Refer.of<User>("Ci0SIv0cYtsy94cy6THeI6HIjG5fnm")
//        return userService.updateUser(user, body.email) ?: throw NotFound(code = NotFound.Codes.ResourceNotFound, details = mapOf("type" to User::class.simpleName, "id" to user.sys.id))
//    }



    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER') and #accountId == principal.sys.id")
    @Operation(summary = "delete user")
    suspend fun delete(
        exchange: ServerWebExchange,
        auth: AccessToken,
        @PathVariable accountId: String
    ) {
        val user = auth.principal.asRef()
        userService.deleteUser(auth.principal.asRef())?.let {
            if (it.sys.sns == null) {
                return
            }

            val provider = findEnumEntryByValue<SocialAccountInfo.SocialAccountType>(it.sys.sns!!.type) ?: throw InternalServerError("Not supported social type.", InternalServerError.Codes.MissingData, mapOf("social" to it.sys.sns!!.type))
            socialAuthManagerFactory.get(provider).unlink(it.sys.sns!!.accessToken, it.sys.sns!!.refreshToken)
        } ?: throw NotFound(code = NotFound.Codes.ResourceNotFound, details = mapOf("type" to user.sys.targetType, "id" to user.sys.id))

        return
    }



    data class UpdateUserVO(
        @field:Email
        val email: String
    )
}