package satisfied.be.dont.pow.core.model

import org.apache.commons.lang3.StringUtils
import satisfied.be.dont.pow.core.util.Base62
import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.*

interface Identity {
    val id: String
    val type: String



    companion object {

        private val random = Random()

        fun generateUid(): String {
            val hostName = StringUtils.left(InetAddress.getLocalHost().hostName, 10).toByteArray(Charsets.UTF_8)
            val buffer = ByteBuffer.allocate(hostName.size + Long.SIZE_BYTES + Int.SIZE_BYTES).apply {
                put(hostName)
                putLong(hostName.size, Date().time)
                putInt(hostName.size + Long.SIZE_BYTES, random.nextInt())
            }
            return Base62.encode(buffer.array())
        }
    }
}