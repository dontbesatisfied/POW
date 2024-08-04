package satisfied.be.dont.pow.core.crypto

interface ICrypto {
    fun encrypt(text: String): String
    fun decrypt(text: String): String
}