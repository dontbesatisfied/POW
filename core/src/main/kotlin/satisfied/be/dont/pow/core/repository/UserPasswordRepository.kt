package satisfied.be.dont.pow.core.repository

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import satisfied.be.dont.pow.core.CoreProperties
import satisfied.be.dont.pow.core.crypto.SHA256
import satisfied.be.dont.pow.core.model.IEntity
import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.model.UserPassword
import java.util.*

@Repository
class UserPasswordRepository: IUserPasswordRepository, IRepository<UserPassword> {

    @Autowired
    private lateinit var db: ReactiveMongoTemplate

    @Autowired
    private lateinit var coreProperties: CoreProperties

    private val passwordEncryptor by lazy { SHA256(coreProperties.security.password.salt) }



    override suspend fun create(userPassword: UserPassword): UserPassword {
        userPassword.password = passwordEncryptor.encrypt(userPassword.password)

        return db.insert(userPassword).awaitSingle()
    }



    override suspend fun findOne(user: Refer<User>): UserPassword? {
        return db.findOne(
            Query(
                Criteria.where("sys.type").`is`(UserPassword::class.simpleName).and("sys.user.sys.id").`is`(user.sys.id)
            ),
            UserPassword::class.java
        ).awaitSingle()
    }



    override suspend fun deleteOneByRefAndField(
        actor: Refer<out IEntity>,
        ref: Refer<out IEntity>,
        field: String
    ): UserPassword? {
        return db.findAndModify(
            Query(
                Criteria.where("sys.type").`is`(UserPassword::class.simpleName).and("$field.sys.id").`is`(ref.sys.id).and("$field.sys.targetType").`is`(ref.sys.targetType)
            ),
            Update().apply {
                set("sys.type", "Deleted${UserPassword::class.simpleName}")
                set("sys.updatedAt", Date())
                set("sys.updatedBy", actor)
            },
            FindAndModifyOptions().apply { returnNew(true) },
            UserPassword::class.java
        ).awaitSingleOrNull()
    }
}