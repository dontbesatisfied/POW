package satisfied.be.dont.pow.core.exception

// 503
class ServiceUnavailable(
    message: String = "Server is not ready to handle the request.",
    code: Codes = Codes.ServerNotReady,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 503 Not found
        ServerNotReady
    }
}