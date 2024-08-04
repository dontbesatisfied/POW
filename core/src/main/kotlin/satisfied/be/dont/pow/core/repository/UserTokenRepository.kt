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
import satisfied.be.dont.pow.core.model.IEntity
import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.model.User
import satisfied.be.dont.pow.core.model.UserToken
import java.util.*

@Repository
class UserTokenRepository: IUserTokenRepository, IRepository<UserToken> {

    @Autowired
    private lateinit var db: ReactiveMongoTemplate



    override suspend fun create(userToken: UserToken): UserToken {
        return db.insert(userToken).awaitSingle()
    }



    override suspend fun findOne(accessToken: String, refreshToken: String): UserToken? {
        return db.findOne(
            Query(
                Criteria.where("sys.type").`is`(UserToken::class.java.simpleName).and("sys.accessToken").`is`(accessToken).and("sys.refreshToken").`is`(refreshToken)
            ),
            UserToken::class.java
        ).awaitSingleOrNull()
    }



    override suspend fun updateOne(user: Refer<User>, accessToken: String, refreshToken: String): UserToken? {
        return db.findAndModify(
            Query(
                Criteria.where("sys.type").`is`(UserToken::class.simpleName).and("sys.createdBy.sys.id").`is`(user.sys.id)
            ),
            Update().apply {
                set("sys.accessToken", accessToken)
                set("sys.refreshToken", refreshToken)
                set("sys.updatedAt", Date())
                set("sys.updatedBy", user)
            },
            FindAndModifyOptions().apply {
                returnNew(true)
            },
            UserToken::class.java
        ).awaitSingleOrNull()
    }



    override suspend fun deleteOneByRefAndField(
        actor: Refer<out IEntity>,
        ref: Refer<out IEntity>,
        field: String
    ): UserToken? {
        return db.findAndModify(
            Query(
                Criteria.where("sys.type").`is`(UserToken::class.simpleName).and("$field.sys.id").`is`(ref.sys.id).and("$field.sys.targetType").`is`(ref.sys.targetType)
            ),
            Update().apply {
                set("sys.type", "Deleted${UserToken::class.simpleName}")
                set("sys.updatedAt", Date())
                set("sys.updatedBy", actor)
            },
            FindAndModifyOptions().apply { returnNew(true) },
            UserToken::class.java
        ).awaitSingleOrNull()
    }
}