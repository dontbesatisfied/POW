package satisfied.be.dont.pow.core.exception

// 409
class Conflict(
    message: String = "The resource exists already.",
    code: Codes = Codes.Conflict,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 409 Conflict
        Conflict
    }
}