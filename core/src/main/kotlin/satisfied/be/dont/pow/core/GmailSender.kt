package satisfied.be.dont.pow.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Component
@ConditionalOnProperty(prefix = "spring.mail", name = ["password"], matchIfMissing = false)
class GmailSender: MailSender {

    @Autowired
    private lateinit var mailSender: JavaMailSender



    override suspend fun sendHTML(message: MailSender.HTMLMessage) {
        val mimeMessage = mailSender.createMimeMessage()
        MimeMessageHelper(mimeMessage, Charsets.UTF_8.name()).apply {
            if (!message.from.isNullOrEmpty()) setFrom(message.from)
            setTo(message.to)
            setSubject(message.subject)
            setText(message.body, true)
        }

        return suspendCoroutine { cont ->
            runCatching {
                mailSender.send(mimeMessage)
            }.onSuccess {
                cont.resume(Unit)
            }.onFailure {
                cont.resumeWithException(it)
            }
        }

    }
}