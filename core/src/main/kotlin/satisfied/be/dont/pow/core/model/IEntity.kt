package satisfied.be.dont.pow.core.model

import java.util.*

interface IEntity {
    val sys: ISys


    interface ISys: Identity {
        val createdBy: IRefer<out IEntity>
        val createdAt: Date
        val updatedBy: IRefer<out IEntity>
        val updatedAt: Date
    }
}