package satisfied.be.dont.pow.core.repository

import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.model.UserPassword

interface IUserPasswordRepository: IRepository<UserPassword> {
    suspend fun create(userPassword: UserPassword): UserPassword

    suspend fun findOne(user: Refer<User>): UserPassword?
}