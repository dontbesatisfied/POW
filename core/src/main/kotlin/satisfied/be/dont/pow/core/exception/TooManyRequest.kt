package satisfied.be.dont.pow.core.exception

// 429
class TooManyRequest(
    message: String = "Too many requests in a given amount of time.",
    code: Codes = Codes.TooManyRequest,
    details: Any? = null,
    logLevel: LogLevel = LogLevel.Error
) : Exception(message, code.toString(), details, logLevel) {
    enum class Codes {
        // 429 TooManyRequest
        TooManyRequest,
        LimitExceeded,
        RateLimitExceeded
    }
}