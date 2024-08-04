package satisfied.be.dont.pow.core.extension

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.reflect.KClass

class Json {

    companion object {
        private val objectMapper = jacksonObjectMapper().apply {
//            this.dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//            this.setTimeZone(TimeZone.getTimeZone("UTC"))
//            this.registerModule(JavaTimeModule())
        }

        private val invalidateIgnoreObjectMapper = jacksonObjectMapper().apply {
            setAnnotationIntrospector(object : JacksonAnnotationIntrospector() {
                override fun hasIgnoreMarker(m: AnnotatedMember?): Boolean {
                    return false
                }
            })
        }



        fun Any.toJson(noIgnore: Boolean = false): String {
            val mapper = if (noIgnore) invalidateIgnoreObjectMapper else objectMapper
            return mapper.writeValueAsString(this)
        }



        fun <T : Any> KClass<T>.fromJson(json: String): T? {
            return try {
                objectMapper.readValue(json, this.java)
            } catch (e: Throwable) {
//                logger.error(mapOf("error" to "Failed to convert from JSON to Object.", "message" to e.message).toJson())
                null
            }
        }

        @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
        inline fun <reified T> fromJson(json: String): T? {
            return try {
                objectMapper.readValue(json, object: TypeReference<T>() {})
            } catch (e: Throwable) {
                null
            }
        }



        @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
        inline fun <reified T> fromHashMap(hashMap: LinkedHashMap<*, *>): T? {
            return try {
                objectMapper.convertValue(hashMap, object: TypeReference<T>() {})
            } catch (e: Throwable) {
                null
            }
        }
    }
}