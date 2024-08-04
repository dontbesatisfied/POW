package satisfied.be.dont.pow.core.config

import org.bson.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories
@ConditionalOnProperty("spring.data.mongodb.uri", matchIfMissing = false)
class MongoConfig {

    @Bean
    @ConditionalOnMissingBean(ReactiveMongoTemplate::class)
    fun reactiveMongoTemplate(
        reactiveMongoDatabaseFactory: ReactiveMongoDatabaseFactory,
        mongoConverter: MappingMongoConverter
    ): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(reactiveMongoDatabaseFactory, mongoConverter)
    }



    @Bean
    fun reactiveMongoConverter(
        mongoMappingContext: MongoMappingContext,
        customConversions: MongoCustomConversions
    ): MappingMongoConverter {
        return MappingMongoConverter(ReactiveMongoTemplate.NO_OP_REF_RESOLVER, mongoMappingContext).apply {
            // '_class' 필드를 제거하는 설정
            setTypeMapper(DefaultMongoTypeMapper(null))
            setCustomConversions(customConversions)
        }
    }



    @Bean
    fun mongoCustomConversions(converters: List<Converter<*, *>>): MongoCustomConversions {
        return MongoCustomConversions(converters)
    }


//    @Component
//    @ReadingConverter
//    class MongoDateConverter: Converter<Date, Date> {
//        override fun convert(source: Date): Date? {
//            return source
//        }
//
//    }
}