package satisfied.be.dont.pow.core.exception

// 408
class RequestTimeout(
    message: String = "The request is taking too long to complete",
    code: Codes = Codes.Timeout,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 408 Unauthorized
        Timeout
    }
}