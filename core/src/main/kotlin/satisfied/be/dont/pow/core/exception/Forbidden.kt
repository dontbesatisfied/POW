package satisfied.be.dont.pow.core.exception

// 403
class Forbidden(
    message: String = "Access not allowed.",
    code: Codes = Codes.Forbidden,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 403 Forbidden
        Forbidden
    }
}