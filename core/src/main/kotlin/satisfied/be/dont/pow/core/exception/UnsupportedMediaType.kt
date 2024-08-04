package satisfied.be.dont.pow.core.exception

// 415
class UnsupportedMediaType(
    message: String = "Unsupported media type.",
    code: Codes = Codes.UnsupportedMediaType,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 415 UnsupportedMediaType
        UnsupportedMediaType
    }
}