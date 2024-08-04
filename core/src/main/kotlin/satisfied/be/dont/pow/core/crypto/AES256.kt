package satisfied.be.dont.pow.core.crypto

import org.apache.commons.codec.binary.Base64
import java.security.Key
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

open class AES256(
    secret: String
): ICrypto {
    private val key: Key
    private val iv: AlgorithmParameterSpec



    init {
        val byteSecret = secret.toByteArray()
        val byteKey = ByteArray(32) {
            try {
                byteSecret[it]
            } catch (e: Throwable) {
                45 // '-'
            }
        }

        this.key = SecretKeySpec(byteKey, "AES")
        this.iv = IvParameterSpec(byteKey.sliceArray(0 until 16))
    }



    override fun encrypt(text: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv)
        val encrypted = cipher.doFinal(text.toByteArray())

        return Base64.encodeBase64(encrypted).toString(Charsets.UTF_8)
    }



    override fun decrypt(text: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, this.key, this.iv)

        return cipher.doFinal(Base64.decodeBase64(text.toByteArray())).toString(Charsets.UTF_8)
    }
}