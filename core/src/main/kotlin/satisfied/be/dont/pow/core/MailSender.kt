package satisfied.be.dont.pow.core

import jakarta.validation.constraints.Email

interface MailSender {

    suspend fun sendHTML(message: HTMLMessage)

    interface Message {
        val to: String
        val subject: String
        val body: Any
        val from: String?
    }

    data class HTMLMessage(
        @field:Email
        override val to: String,
        override val subject: String,
        override val body: String,
        @field:Email
        override val from: String? = null,
    ): Message
}