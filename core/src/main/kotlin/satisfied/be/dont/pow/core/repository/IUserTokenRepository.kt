package satisfied.be.dont.pow.core.repository

import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.model.UserToken

interface IUserTokenRepository: IRepository<UserToken> {
    suspend fun create(userToken: UserToken): UserToken

    suspend fun findOne(accessToken: String, refreshToken: String): UserToken?

    suspend fun updateOne(user: Refer<User>, accessToken: String, refreshToken: String): UserToken?
}