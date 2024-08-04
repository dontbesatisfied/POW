package satisfied.be.dont.pow.core.web.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.web.security.permission.Authority

class AccessToken(
    private val user: User
): AbstractAuthenticationToken(if (user.sys.isAdmin) Authority.ADMIN_AUTHORITIES else Authority.USER_AUTHORITIES) {

    init {
        this.isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return Unit
    }

    override fun getPrincipal(): User {
        // TODO: 이부분 확장 및  PermissionEvaluator 구현체만들면 hasPermission 을 사용해서 ABAC 가능할듯.
        return user
    }

}