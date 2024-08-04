package satisfied.be.dont.pow.core.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = IUserPassword.COLLECTION_NAME)
@CompoundIndexes(
    CompoundIndex(def = "{ 'sys.type': 1, 'sys.id': 1 }", unique = true, background = true),
    CompoundIndex(def = "{ 'sys.type': 1, 'sys.user.sys.id': 1 }", unique = true, background = true)
)
interface IUserPassword: IEntity {

    override val sys: ISys
    val password: String

    interface ISys: IEntity.ISys {
        val resetToken: String?
        val user: Refer<User>
    }


    companion object {
        const val COLLECTION_NAME = "user_passwords"
    }
}