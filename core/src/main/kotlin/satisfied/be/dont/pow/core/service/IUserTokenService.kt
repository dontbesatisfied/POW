package satisfied.be.dont.pow.core.service

import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.model.UserToken

interface IUserTokenService {
    suspend fun createUserToken(user: Refer<User>): UserToken
    suspend fun updateUserToken(user: Refer<User>): UserToken?
    suspend fun updateUserToken(accessToken: String, refreshToken: String): UserToken?
}