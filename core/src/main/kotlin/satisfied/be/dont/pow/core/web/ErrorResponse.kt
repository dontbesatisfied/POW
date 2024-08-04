package satisfied.be.dont.pow.core.web

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import satisfied.be.dont.pow.core.exception.Exception
import java.io.Serializable
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrorResponse private constructor(
    @field:Schema(description = "unique id per request.", example = "DZBsD5JF3k3UiiXEjefJs4pJmnCvsteVDx6FwujPo9u")
    val requestId: String?,
    val sys: Sys,
    val details: Any? = null,
    @field:Schema(description = "Error message.", example = "Invalid password.")
    val message: String?
): Serializable {

    constructor(
        requestId: String? = null,
        id: String,
        type: String,
        code: String? = null,
        message: String? = null,
        details: Any? = null
    ) : this(requestId, Sys(id, type, code), details, message)


    constructor(
        requestId: String?,
        exception: Exception
    ) : this(requestId, Sys(exception.id, exception.javaClass.simpleName, exception.code), exception.details, exception.message)


    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Sys(
        @field:Schema(description = "Error Id for tracing.", example = "tmhwQZ6GWzxBoQKOACuNoOYf3609t2QC")
        val id: String,
        @field:Schema(description = "Category of error.", example = "BadRequest")
        val type: String,
        @field:Schema(description = "Detail code of error.", example = "InvalidValue")
        val code: String?,
        val timestamp: Long = Date().time
    ): Serializable
}