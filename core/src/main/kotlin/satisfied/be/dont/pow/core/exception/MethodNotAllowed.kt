package satisfied.be.dont.pow.core.exception

// 405
class MethodNotAllowed(
    message: String = "The access token is invalid.",
    code: Codes = Codes.MethodNotFound,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 405
        MethodNotFound
    }
}