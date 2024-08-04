package satisfied.be.dont.pow.core

import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import satisfied.be.dont.pow.core.annotation.AutoDeleteBy
import satisfied.be.dont.pow.core.exception.NotFound
import satisfied.be.dont.pow.core.model.IEntity
import satisfied.be.dont.pow.core.model.Refer
import satisfied.be.dont.pow.core.repository.IRepository
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

@Component
class ModelMapper {

    @Autowired
    private lateinit var context: ApplicationContext

    private val repositoryMap by lazy {
        val map: HashMap<String, IRepository<IEntity>> = HashMap()

        Reflections("satisfied.be.dont.pow.core.repository").getSubTypesOf(IRepository::class.java).forEach {  repository ->
            repository.genericInterfaces.forEach { type ->
                if (type !is ParameterizedType) { return@forEach }
                if (type.rawType != IRepository::class.java) { return@forEach }

                val model = type.actualTypeArguments.firstOrNull() ?: return@forEach
                val modelName = (model as Class<IEntity>).kotlin.simpleName ?: return@forEach

                context.getBeansOfType(repository).forEach {
                    map[modelName] = it.value as IRepository<IEntity>
                }
            }
        }

        map
    }

    private val relationsMap by lazy {
        val map: HashMap<String, MutableList<Relation>> = HashMap()
        Reflections("satisfied.be.dont.pow.core.model").getSubTypesOf(IEntity::class.java).forEach { model ->
            model.getDeclaredAnnotationsByType(AutoDeleteBy::class.java).forEach { annotation ->
                if (map[annotation.parentModel.simpleName!!].isNullOrEmpty()) {
                    map[annotation.parentModel.simpleName!!] = mutableListOf(Relation(annotation.referenceField, annotation.nullable, model.kotlin))
                    return@forEach
                }
                map[annotation.parentModel.simpleName!!]!!.add(Relation(annotation.referenceField, annotation.nullable, model.kotlin))
            }
        }

        map
    }



    suspend fun deleteChildEntities(actor: Refer<out IEntity>, entity: Refer<out IEntity>) {
        relationsMap[entity.sys.targetType]?.forEach {

            val res = repositoryMap[it.model.simpleName]?.deleteOneByRefAndField(actor, entity, it.referenceField)
            if (!it.nullable && res == null) {
                throw NotFound(details = mapOf("type" to it.model.simpleName, "${it.referenceField}.sys.id" to entity.sys.id))
            }
        }
    }



    data class Relation(
        val referenceField: String,
        val nullable: Boolean,
        val model: KClass<out IEntity>
    )
}