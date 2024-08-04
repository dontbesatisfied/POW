package satisfied.be.dont.pow.core.exception

// 401
class Unauthorized(
    message: String = "The access token is invalid.",
    code: Codes = Codes.Unauthorized,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 401 Unauthorized
        Unauthorized
    }
}