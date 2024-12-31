package xyz.atom7.recoveryServer.serialization

import java.time.LocalDateTime

data class RecoveryCode(
    val id: Long,
    val code: String,
    val username: String,
    val expired: Boolean,
    val creationDate: LocalDateTime,
    val expirationDate: LocalDateTime
)
