package satisfied.be.dont.pow.core.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import satisfied.be.dont.pow.core.CoreProperties
import satisfied.be.dont.pow.core.MailSender
import satisfied.be.dont.pow.core.ModelMapper
import satisfied.be.dont.pow.core.crypto.SHA256
import satisfied.be.dont.pow.core.exception.Conflict
import satisfied.be.dont.pow.core.exception.Exception
import satisfied.be.dont.pow.core.exception.InternalServerError
import satisfied.be.dont.pow.core.exception.NotFound
import satisfied.be.dont.pow.core.exception.Unauthorized
import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.Refer.Companion.asRef
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.model.UserPassword
import satisfied.be.dont.pow.core.repository.IUserPasswordRepository
import satisfied.be.dont.pow.core.repository.IUserRepository
import satisfied.be.dont.pow.core.util.Base62
import java.util.*

@Service
class UserService: IUserService {

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userPasswordRepo: IUserPasswordRepository

    @Autowired
    private lateinit var mailSender: MailSender

    @Autowired
    private lateinit var coreProperties: CoreProperties

    @Autowired
    private lateinit var modelMapper: ModelMapper

    private val passwordEncryptor by lazy { SHA256(coreProperties.security.password.salt) }



    override suspend fun createUser(name: String, socialAccountInfo: SocialAccountInfo): User {
        if (userRepo.exists(socialAccountInfo)) {
            throw Conflict(details = mapOf("type" to socialAccountInfo.type, "id" to socialAccountInfo.id))
        }

        return userRepo.create(
            User(name, socialAccountInfo)
        )
    }



    override suspend fun createUser(name: String, email: String, password: String): User {
        if (userRepo.exists(email)) {
            throw Conflict(code = Conflict.Codes.Conflict, details = mapOf("email" to email))
        }

        val activateToken = Base62.encode(UUID.randomUUID().toString().uppercase(Locale.ENGLISH).replace("-", ""))

        return userRepo.create(User(name, email, activateToken)).also {
            userPasswordRepo.create(UserPassword(it.asRef(), password))

            val redirectUrl = UriComponentsBuilder.fromUriString(coreProperties.callback.accountActivateBaseUri).apply {
                queryParam("token", activateToken)
            }.buildAndExpand(it.sys.id).toUriString()

            val subject = ClassPathResource("email/register.subject.html").getContentAsString(Charsets.UTF_8)
            val body = ClassPathResource("email/register.body.html").getContentAsString(Charsets.UTF_8)
                .replace("{{ACTIVATE_LINK}}", redirectUrl)
            mailSender.sendHTML(MailSender.HTMLMessage(email, subject, body))
        }

    }



    override suspend fun getUser(socialAccountInfo: SocialAccountInfo): User? {
        return userRepo.findOne(socialAccountInfo)
    }



    override suspend fun getUser(user: Refer<User>): User? {
        return userRepo.findOne(user)
    }



    override suspend fun login(email: String, password: String): User? {
        val user = userRepo.findOne(email) ?: return null
        if (!user.sys.isActivated) {
            throw Unauthorized(
                "This account is not activated yet.",
                details = mapOf("type" to User::class.simpleName, "id" to user.sys.id)
            )
        }

        val userPassword = userPasswordRepo.findOne(user.asRef()) ?: return null
        if (!passwordEncryptor.verify(password, userPassword.password)) {
            return null
        }

        return user
    }



    override suspend fun updateUser(user: Refer<User>, email: String): User? {
        return userRepo.updateOne(user, email)
    }



    override suspend fun activateUser(user: Refer<User>, activateToken: String): User? {
        if (!userRepo.exists(user, activateToken)) {
            throw Unauthorized("The token is invalid.", details = mapOf("activateToken" to activateToken))
        }

        return userRepo.updateOne(user, true)
    }



    override suspend fun deleteUser(user: Refer<User>): User? {
        userRepo.findOne(user) ?: throw NotFound(details = mapOf("type" to User::class.simpleName, "id" to user.sys.id))

        try {
            modelMapper.deleteChildEntities(user, user)
        } catch (e: Exception) {
            throw InternalServerError("Failed to delete children", InternalServerError.Codes.WriteError, e)
        }

        return userRepo.deleteOne(user)
    }
}