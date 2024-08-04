package satisfied.be.dont.pow.core.exception

// 500
class InternalServerError(
    message: String = "Unexpected exception occurred.",
    code: Codes = Codes.InternalServerError,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 500 InternalServerError
        InternalServerError,
        MissingData,
        WriteError,
        ReadError,
        ProcessingError
    }
}