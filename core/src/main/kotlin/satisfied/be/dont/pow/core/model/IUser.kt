package satisfied.be.dont.pow.core.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = IUser.COLLECTION_NAME)
@CompoundIndexes(
    CompoundIndex(def = "{ 'sys.type': 1, 'sys.id': 1 }", unique = true, background = true),
    CompoundIndex(def = "{ 'sys.type': 1, 'email': 1 }", unique = true, background = true, partialFilter = "{ 'email': { '\$exists': true }, 'sys.type': 'User' }"),
    CompoundIndex(def = "{ 'sys.type': 1, 'sys.sns.type': 1, 'sys.sns.id': 1 }", unique = true, background = true, partialFilter = "{ 'sys.sns': { '\$exists': true }, 'sys.type': 'User' }"),
)
interface IUser: IEntity {

    override val sys: ISys
    val name: String
    val email: String?

    interface ISys: IEntity.ISys {
        val isActivated: Boolean
        val activateToken: String?
        val isAdmin: Boolean
        val sns: SocialAccountInfo?
    }


    companion object {
        const val COLLECTION_NAME = "users"
    }
}