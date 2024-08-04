package satisfied.be.dont.pow.core.repository

import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import satisfied.be.dont.pow.core.model.User

interface IUserRepository: IRepository<User> {

    suspend fun create(user: User): User

    suspend fun exists(user: Refer<User>): Boolean
    suspend fun exists(sns: SocialAccountInfo): Boolean
    suspend fun exists(email: String): Boolean
    suspend fun exists(user: Refer<User>, activateToken: String): Boolean
    suspend fun findOne(user: Refer<User>): User?
    suspend fun findOne(sns: SocialAccountInfo): User?
    suspend fun findOne(email: String): User?

    suspend fun updateOne(user: Refer<User>, email: String): User?
    suspend fun updateOne(user: Refer<User>, isActivated: Boolean): User?

    suspend fun deleteOne(user: Refer<User>): User?
}