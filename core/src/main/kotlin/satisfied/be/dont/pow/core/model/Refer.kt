package satisfied.be.dont.pow.core.model

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.mongodb.core.mapping.Field
import kotlin.reflect.KClass

data class Refer<T: IEntity>(
    override val sys: Sys<T>
): IRefer<T> {

    fun asMap(): Map<String, Map<String, String>> {
        return mapOf(
            "sys" to mapOf(
                "id" to sys.id,
                "type" to sys.type,
                "targetType" to sys.targetType
            )
        )
    }



    data class Sys<K: IEntity>(
        @field:Schema(example = "Entity")
        override val targetType: String,
        @Field(name = "id")
        @field:Schema(description = "related resource id", example = "Ci0SIv0cYtsy94cy6THeI6HIjG5fnm")
        override val id: String,
        @field:Schema(example = "Refer")
        override val type: String = Refer::class.simpleName!!
    ): IRefer.ISys {

        constructor(id: String, targetType: KClass<K>): this(targetType.simpleName!!, id)



        companion object {

            inline fun <reified T: IEntity> of(id: String): Sys<T> = Sys(id, T::class)
        }
    }



    companion object {

        inline fun <reified T: IEntity> of(id: String): Refer<T> = Refer(Sys.of<T>(id))

        inline fun <reified T: IEntity> T.asRef() : Refer<T> {
            return Refer(Sys(this.sys.id, T::class))
        }
    }
}
