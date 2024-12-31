package xyz.atom7.recoveryServer.serialization

data class SerialException(
    val status: Int,
    val message: String,
    val timestamp: Long
)
