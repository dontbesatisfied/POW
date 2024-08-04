package satisfied.be.dont.pow.core.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import satisfied.be.dont.pow.core.crypto.Authenticator
import satisfied.be.dont.pow.core.exception.BadRequest
import satisfied.be.dont.pow.core.exception.NotFound
import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.model.UserToken
import satisfied.be.dont.pow.core.repository.IUserRepository
import satisfied.be.dont.pow.core.repository.IUserTokenRepository

@Service
class UserTokenService: IUserTokenService {

    @Autowired
    private lateinit var userTokenRepo: IUserTokenRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var authenticator: Authenticator



    override suspend fun createUserToken(user: Refer<User>): UserToken {
        if (!userRepo.exists(user)) {
            throw NotFound(details = mapOf("type" to user.sys.type, "id" to user.sys.id))
        }

        val tokens = authenticator.issue(user)

        return userTokenRepo.create(
            UserToken(user, tokens.accessToken, tokens.refreshToken)
        )
    }



    override suspend fun updateUserToken(
        user: Refer<User>,
    ): UserToken? {
        if (!userRepo.exists(user)) {
            throw NotFound(details = mapOf("type" to user.sys.type, "id" to user.sys.id))
        }

        val tokens = authenticator.issue(user)
        return userTokenRepo.updateOne(user, tokens.accessToken, tokens.refreshToken)
    }



    override suspend fun updateUserToken(accessToken: String, refreshToken: String): UserToken? {
        val userToken = userTokenRepo.findOne(accessToken, refreshToken) ?: throw BadRequest(code = BadRequest.Codes.InvalidValue, details = mapOf("accessToken" to accessToken, "refreshToken" to refreshToken))
        val tokens = authenticator.renew(accessToken, refreshToken)

        return userTokenRepo.updateOne(userToken.sys.createdBy, tokens.accessToken, tokens.refreshToken)
    }
}