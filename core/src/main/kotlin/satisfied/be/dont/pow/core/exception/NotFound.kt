package satisfied.be.dont.pow.core.exception

// 404
class NotFound(
    message: String = "The resource could not be found.",
    code: Codes = Codes.ResourceNotFound,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 404 Not found
        APINotFound,
        MethodNotFound,
        ResourceNotFound,
        ResourceExpired
    }
}