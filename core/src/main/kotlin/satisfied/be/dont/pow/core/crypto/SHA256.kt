package satisfied.be.dont.pow.core.crypto

import org.apache.commons.codec.binary.Base64
import java.security.MessageDigest

open class SHA256(
    secret: String
) {
    private val salt: ByteArray

    init {
        val byteSecret = secret.toByteArray()
        val byteKey = ByteArray(32) {
            try {
                byteSecret[it]
            } catch (e: Throwable) {
                45 // '-'
            }
        }
        this.salt = byteKey
    }

    fun encrypt(text: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(this.salt)
        return MessageDigest.getInstance("SHA-256").let {
            it.update(this.salt)
            val combined = text.toByteArray() + this.salt
            Base64.encodeBase64(md.digest(combined)).toString(Charsets.UTF_8)
        }
    }

    fun verify(text: String, encryptText: String): Boolean {
        val hashedInputPassword = encrypt(text)
        return hashedInputPassword == encryptText
    }
}