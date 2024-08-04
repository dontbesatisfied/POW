package satisfied.be.dont.pow.core.exception

// 422
open class Unprocessable(
    message: String = "The request is unprocessable.",
    code: Codes = Codes.Unprocessable,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    // 422 Unprocessable
    enum class Codes {
        Unprocessable,
        NotSupported,
        NotImplemented
    }
}