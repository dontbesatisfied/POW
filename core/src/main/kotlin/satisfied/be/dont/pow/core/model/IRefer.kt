package satisfied.be.dont.pow.core.model

interface IRefer<T: IEntity> {
    val sys: ISys

    interface ISys: Identity {
        val targetType: String
    }
}