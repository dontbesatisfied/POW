package satisfied.be.dont.pow.core.exception

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import satisfied.be.dont.pow.core.util.Base62
import java.io.Serializable

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("stackTrace", "suppressed", "localizedMessage")
open class Exception(
    message: String,
    val code: String?,
    val details: Any? = null,
    val logLevel: LogLevel = LogLevel.Error
) : RuntimeException(message), Serializable {
    val id: String

    init {
        val fileName = removeFileExtension(stackTrace[0].fileName!!)
        val caller = Base62.encode("${fileName}:${stackTrace[0].lineNumber}")
        id = caller
    }



    private fun removeFileExtension(fileName: String): String {
        val lastIndex = fileName.lastIndexOf('.')
        if (lastIndex != -1) {
            return fileName.substring(0, lastIndex)
        }
        return fileName
    }
}



enum class LogLevel {
    Debug,
    Info,
    Warn,
    Error,
    None
}