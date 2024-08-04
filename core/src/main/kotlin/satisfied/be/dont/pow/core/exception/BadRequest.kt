package satisfied.be.dont.pow.core.exception

// 400
class BadRequest(
    message: String = "The request is bad.",
    code: Codes = Codes.BadRequest,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
): Exception(message, code.toString(), details, logLevel) {

    enum class Codes {
        BadRequest,
        InvalidValue,
        ValidationError,
        MissingObject,
        PolicyViolation
    }
}