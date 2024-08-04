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
import satisfied.be.dont.pow.core.crypto.AES256
import satisfied.be.dont.pow.core.model.IEntity
import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.SocialAccountInfo
import satisfied.be.dont.pow.core.model.User
import java.util.*

@Repository
class UserRepository: IUserRepository, IRepository<User> {

    @Autowired
    private lateinit var db: ReactiveMongoTemplate

    @Autowired
    private lateinit var coreProperties: CoreProperties

    private val emailEncryptor by lazy { AES256(coreProperties.security.email.salt) }



    override suspend fun create(user: User): User {
        val originalEmail = user.email
        if (!user.email.isNullOrEmpty()) {
            user.email = emailEncryptor.encrypt(user.email!!)
        }

        return db.insert(user).awaitSingle().apply {
            if (!this.email.isNullOrEmpty()) { this.email = originalEmail }
        }
    }



    override suspend fun exists(user: Refer<User>): Boolean {
        return db.exists(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("sys.id").`is`(user.sys.id)
            ),
            User::class.java
        ).awaitSingle()
    }




    override suspend fun exists(sns: SocialAccountInfo): Boolean {
        return db.exists(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("sys.sns.type").`is`(sns.type).and("sys.sns.id").`is`(sns.id)
            ),
            User::class.java
        ).awaitSingle()
    }



    override suspend fun exists(email: String): Boolean {
        return db.exists(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("email").`is`(emailEncryptor.encrypt(email))
            ),
            User::class.java
        ).awaitSingle()
    }



    override suspend fun exists(user: Refer<User>, activateToken: String): Boolean {
        return db.exists(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("sys.id").`is`(user.sys.id).and("sys.activateToken").`is`(activateToken)
            ),
            User::class.java
        ).awaitSingle()
    }



    override suspend fun findOne(user: Refer<User>): User? {
        return db.findOne(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("sys.id").`is`(user.sys.id)
            ),
            User::class.java
        ).awaitSingleOrNull()?.apply {
            if (!this.email.isNullOrEmpty()) { this.email = emailEncryptor.decrypt(this.email!!) }
        }
    }



    override suspend fun findOne(sns: SocialAccountInfo): User? {
        return db.findOne(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("sys.sns.type").`is`(sns.type).and("sys.sns.id").`is`(sns.id)
            ),
            User::class.java,
        ).awaitSingleOrNull()?.apply {
            if (!this.email.isNullOrEmpty()) { this.email = emailEncryptor.decrypt(this.email!!) }
        }
    }



    override suspend fun findOne(email: String): User? {
        return db.findOne(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("email").`is`(emailEncryptor.encrypt(email))
            ),
            User::class.java
        ).awaitSingleOrNull()?.apply {
            if (!this.email.isNullOrEmpty()) { this.email = emailEncryptor.decrypt(this.email!!) }
        }
    }



    override suspend fun updateOne(user: Refer<User>, email: String): User? {
        return db.findAndModify(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("sys.id").`is`(user.sys.id)
            ),
            Update().apply {
                set("sys.updatedAt", Date())
                set("sys.updatedBy", user)
                set("email", emailEncryptor.encrypt(email))
            },
            FindAndModifyOptions().apply {
                returnNew(true)
            },
            User::class.java
        ).awaitSingleOrNull()?.apply {
            if (!this.email.isNullOrEmpty()) { this.email = email }
        }
    }



    override suspend fun updateOne(user: Refer<User>, isActivated: Boolean): User? {
        return db.findAndModify(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("sys.id").`is`(user.sys.id)
            ),
            Update().apply {
                set("sys.updatedAt", Date())
                set("sys.updatedBy", user)
                set("sys.isActivated", isActivated)
                unset("sys.activateToken")
            },
            FindAndModifyOptions().apply {
                returnNew(true)
            },
            User::class.java
        ).awaitSingleOrNull()?.apply {
            if (!this.email.isNullOrEmpty()) { this.email = emailEncryptor.decrypt(this.email!!) }
        }
    }



    override suspend fun deleteOne(user: Refer<User>): User? {
        return db.findAndModify(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("sys.id").`is`(user.sys.id)
            ),
            Update().apply {
                set("sys.type", "Deleted${User::class.simpleName}")
                set("sys.updatedAt", Date())
                set("sys.updatedBy", user)
            },
            FindAndModifyOptions().apply { returnNew(true) },
            User::class.java
        ).awaitSingleOrNull()
    }



    override suspend fun deleteOneByRefAndField(
        actor: Refer<out IEntity>,
        ref: Refer<out IEntity>,
        field: String
    ): User? {
        return db.findAndModify(
            Query(
                Criteria.where("sys.type").`is`(User::class.simpleName).and("$field.sys.id").`is`(ref.sys.id).and("$field.sys.targetType").`is`(ref.sys.targetType)
            ),
            Update().apply {
                set("sys.type", "Deleted${User::class.simpleName}")
                set("sys.updatedAt", Date())
                set("sys.updatedBy", actor)
            },
            FindAndModifyOptions().apply { returnNew(true) },
            User::class.java
        ).awaitSingleOrNull()
    }
}