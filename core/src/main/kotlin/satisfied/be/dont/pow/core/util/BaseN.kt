package satisfied.be.dont.pow.core.util


class BaseN(
    private val characterSet: String
) {
    private val N = characterSet.length.toBigInteger()



    fun encode(input: ByteArray): String {
        var num = input.fold(0.toBigInteger()) { acc, byte ->
            (acc shl 8) or (byte.toInt() and 0xff).toBigInteger()
        }
        val encoded = StringBuilder()

        while (num > 0.toBigInteger()) {
            val remainder = (num % N).toInt()
            num /= N
            encoded.append(characterSet[remainder])
        }

        return encoded.reverse().toString()
    }



    fun decode(input: String): ByteArray {
        val num = input.fold(0.toBigInteger()) { acc, char ->
            acc * N + characterSet.indexOf(char).toBigInteger()
        }

        val byteArray = num.toByteArray()
        // Remove leading zero byte if present
        // BigInteger를 byteArray로 변환할 때 앞에 0 byte가 붙는 이유는 부호를 나타내기 위해서입니다. BigInteger는 Java의 BigInteger 클래스처럼 부호 있는 정수를 표현할 수 있습니다. 따라서 음수를 나타낼 때는 부호 비트를 유지해야 하고, 양수일 때는 부호 비트를 명확하게 0으로 설정해야 합니다.
        return if (byteArray.size > 1 && byteArray[0] == 0.toByte()) byteArray.copyOfRange(1, byteArray.size) else byteArray
    }
}
