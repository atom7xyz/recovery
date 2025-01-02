package xyz.atom7.recoveryweb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.atom7.recoveryweb.entities.RecoveryCode
import java.util.*

interface CodeRepository : JpaRepository<RecoveryCode, Long>
{
    fun findRecoveryCodeByUsernameAndExpired(
        username: String,
        expired: Boolean
    ): Optional<RecoveryCode>

    fun findRecoveryCodeByUsernameAndCodeAndExpired(
        username: String,
        code: String,
        expired: Boolean
    ): Optional<RecoveryCode>
}