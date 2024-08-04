package satisfied.be.dont.pow.core.web

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.google.common.io.BaseEncoding
import com.mongodb.MongoSecurityException
import jakarta.validation.ConstraintViolationException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import satisfied.be.dont.pow.core.exception.*
import satisfied.be.dont.pow.core.web.filter.RequestIdFilter

@RestControllerAdvice
class RestControllerAdvice {

    // -----------------------------------------------
    // Custom Exception Handler
    // -----------------------------------------------
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequest::class)
    fun handleException(exchange: ServerWebExchange, e: BadRequest): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.BAD_REQUEST.value())
        }
    }



    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFound::class)
    fun handleException(exchange: ServerWebExchange, e: NotFound): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.NOT_FOUND.value())
        }
    }



    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(Forbidden::class)
    fun handleException(exchange: ServerWebExchange, e: Forbidden): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.FORBIDDEN.value())
        }
    }



    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(Unauthorized::class)
    fun handleException(exchange: ServerWebExchange, e: Unauthorized): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.UNAUTHORIZED.value())
        }
    }



    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(UnsupportedMediaType::class)
    fun handleException(exchange: ServerWebExchange, e: UnsupportedMediaType): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
        }
    }



    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    @ExceptionHandler(RequestTimeout::class)
    fun handleException(exchange: ServerWebExchange, e: RequestTimeout): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.REQUEST_TIMEOUT.value())
        }
    }



    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(Conflict::class)
    fun handleException(exchange: ServerWebExchange, e: Conflict): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.CONFLICT.value())
        }
    }




    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InternalServerError::class)
    fun handleException(exchange: ServerWebExchange, e: InternalServerError): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.INTERNAL_SERVER_ERROR.value())
        }
    }



    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(TooManyRequest::class)
    fun handleException(exchange: ServerWebExchange, e: TooManyRequest): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.TOO_MANY_REQUESTS.value())
        }
    }



    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(Unprocessable::class)
    fun handleException(exchange: ServerWebExchange, e: Unprocessable): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.UNPROCESSABLE_ENTITY.value())
        }
    }



    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailable::class)
    fun handleException(exchange: ServerWebExchange, e: ServiceUnavailable): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, e).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.SERVICE_UNAVAILABLE.value())
        }
    }



    // -----------------------------------------------
    // Missing Value in VO
    // -----------------------------------------------
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServerWebInputException::class)
    fun handleException(exchange: ServerWebExchange, e: ServerWebInputException): ErrorResponse {
        if (e.cause is BaseEncoding.DecodingException) {
            return handleException(exchange, e.cause as BaseEncoding.DecodingException)
        }

        val details = HashMap<String, Any>()
        e.reason?.let {
            details["reason"] = "Data is missing."
        }
        if (e.cause is TypeMismatchException) {
            details["reason"] = "Invalid type of value."
            details["value"] = (e.cause as TypeMismatchException).value!!.toString()
            details["requiredType"] = (e.cause as TypeMismatchException).requiredType?.simpleName.toString()
        } else if (e.cause?.cause is InvalidFormatException) {
            details["reason"] = "Invalid value."
            details["value"] = (e.cause!!.cause as InvalidFormatException).value.toString()
            details["fields"] = (e.cause!!.cause as InvalidFormatException).path.map { it.fieldName }
        } else if(e.cause?.cause is MismatchedInputException) {
            details["reason"] = "Missing value of fields."
            details["fields"] = (e.cause?.cause as MismatchedInputException).path.map { it.fieldName }
        }

        // missing payload
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        val message = e.message ?: "Bad Request"
        return ErrorResponse(
            requestId,
            BadRequest(code = BadRequest.Codes.MissingObject, message = message, details = details)
        ).apply {
//            logger.error(this, e, exchange, HttpStatus.BAD_REQUEST.value())
        }
    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleException(exchange: ServerWebExchange, e: WebExchangeBindException): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        val details = e.bindingResult.fieldErrors.map {
            mapOf(
                "field" to it.field,
                "value" to it.rejectedValue,
                "message" to it.defaultMessage
            )
        }
        return ErrorResponse(
            requestId,
            BadRequest(code = BadRequest.Codes.ValidationError, details = details)
        ).apply {
//            logger.error(this, e, exchange, HttpStatus.BAD_REQUEST.value())
        }
    }



    // -----------------------------------------------
    // Validation Exception
    // -----------------------------------------------
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleException(exchange: ServerWebExchange, e: ConstraintViolationException): ErrorResponse {
        val details = HashMap<String, String>()
        e.constraintViolations.forEach {
            details[it.propertyPath.last().name] = it.message
        }

        // invalid parameter
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(
            requestId,
            BadRequest(code = BadRequest.Codes.ValidationError, message = "Invalid parameter.", details = details)
        ).apply {
//            logger.error(this, e, exchange, HttpStatus.BAD_REQUEST.value())
        }
    }



    // -----------------------------------------------
    // Method Not Found
    // -----------------------------------------------
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(MethodNotAllowedException::class)
    fun handleMethodNotAllowedException(exchange: ServerWebExchange, e: MethodNotAllowedException): ErrorResponse {
        val message = e.message ?: "Method NotFound"
        val details = mapOf(
            "method" to exchange.request.method.name(),
            "remote" to exchange.request.remoteAddress?.toString(),
            "path" to exchange.request.path.toString(),
            "header" to exchange.request.headers,
        )

        // missing method
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(
            requestId,
            MethodNotAllowed(code = MethodNotAllowed.Codes.MethodNotFound, message = message, details = details)
        ).apply {
//            logger.error(this, e, exchange, HttpStatus.METHOD_NOT_ALLOWED.value())
        }
    }



    // -----------------------------------------------
    // API Not Found
    // -----------------------------------------------
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleException(exchange: ServerWebExchange, e: NoResourceFoundException): ErrorResponse {
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, NotFound(code = NotFound.Codes.APINotFound)).apply {
//            logger.log(e.logLevel, this, e, exchange, HttpStatus.BAD_REQUEST.value())
        }
    }



    // -----------------------------------------------
    // Unhandled Exceptions
    // -----------------------------------------------
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable::class)
    fun handleException(exchange: ServerWebExchange, e: Throwable): ErrorResponse {
        val message = if(e is MongoSecurityException) "Security Exception" else e.message
        val requestId = exchange.response.headers.getFirst(RequestIdFilter.requestIdKey)
        return ErrorResponse(requestId, InternalServerError(message = message ?: "Unhandled exception", details = mapOf("type" to e.javaClass.simpleName))).apply {
//            logger.error(this, e, exchange, HttpStatus.INTERNAL_SERVER_ERROR.value())
        }
    }
}