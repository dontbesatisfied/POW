package satisfied.be.dont.pow.core.exception

// 501
open class NotImplemented(
    message: String = "The request is unprocessable.",
    code: Codes = Codes.NotImplemented,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    // 501 NotImplemented
    enum class Codes {
        NotImplemented
    }
}