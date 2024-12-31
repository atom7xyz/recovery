package xyz.atom7.recoveryServer.serialization

import java.time.LocalDateTime

data class Player(
    val id: Long,
    val username: String,
    val address: String,
    val premium: Boolean,
    val clientVersion: String,
    val loginTime: LocalDateTime,
    val recoveryCode: RecoveryCode
)
