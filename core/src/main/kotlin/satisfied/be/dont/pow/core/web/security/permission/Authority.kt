package satisfied.be.dont.pow.core.web.security.permission

import org.springframework.security.core.authority.SimpleGrantedAuthority

class Authority {

    companion object {
        private val ADMIN = SimpleGrantedAuthority("ROLE_ADMIN")
        private val USER = SimpleGrantedAuthority("ROLE_USER")

        val ADMIN_AUTHORITIES = listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        val USER_AUTHORITIES = listOf(SimpleGrantedAuthority("ROLE_USER"))

        val HIERARCHY = "$ADMIN > $USER"
    }
}