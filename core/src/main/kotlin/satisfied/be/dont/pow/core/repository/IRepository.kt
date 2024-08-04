package satisfied.be.dont.pow.core.repository

import satisfied.be.dont.pow.core.model.IEntity
import satisfied.be.dont.pow.core.model.Refer

interface IRepository<T: IEntity> {
    suspend fun deleteOneByRefAndField(actor: Refer<out IEntity>, ref: Refer<out IEntity>, field: String): T?
}