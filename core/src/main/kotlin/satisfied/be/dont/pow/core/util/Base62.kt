package satisfied.be.dont.pow.core.util

class Base62 {

    companion object {

        private val base62 = BaseN("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")



        fun encode(input: ByteArray): String {
            return base62.encode(input)
        }



        fun encode(input: String): String {
            return base62.encode(input.toByteArray())
        }



        fun decode(input: String): String {
            return String(base62.decode(input))
        }

    }
}