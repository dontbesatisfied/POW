package satisfied.be.dont.pow.core.model

import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = IUserToken.COLLECTION_NAME)
@CompoundIndexes(
    CompoundIndex(def = "{ 'sys.type': 1, 'sys.id': 1 }", unique = true, background = true),
    CompoundIndex(def = "{ 'sys.type': 1, 'sys.createdBy.sys.id': 1 }", unique = true, background = true),
    CompoundIndex(def = "{ 'sys.type': 1, 'sys.accessToken': 1, 'sys.refreshToken': 1 }", unique = false, background = true)
)
interface IUserToken: IEntity {

    override val sys: ISys

    interface ISys: IEntity.ISys {
        val accessToken: String
        val refreshToken: String
    }

    companion object {
        const val COLLECTION_NAME = "user_tokens"
    }
}