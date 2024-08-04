package satisfied.be.dont.pow.core.service

import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import satisfied.be.dont.pow.core.model.User

interface IUserService {
    suspend fun createUser(name: String, socialAccountInfo: SocialAccountInfo): User
    suspend fun createUser(name: String, email: String, password: String): User

    suspend fun getUser(socialAccountInfo: SocialAccountInfo): User?
    suspend fun getUser(user: Refer<User>): User?
    suspend fun login(email: String, password: String): User?

    suspend fun updateUser(user: Refer<User>, email: String): User?
    suspend fun activateUser(user: Refer<User>, activateToken: String): User?

    suspend fun deleteUser(user: Refer<User>): User?
}