package xyz.atom7.recoveryweb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.atom7.recoveryweb.entities.Player
import xyz.atom7.recoveryweb.entities.RecoveryCode
import java.util.*

interface PlayerRepository : JpaRepository<Player, Long>
{
    fun findPlayerByUsername(username: String): MutableList<Player>
    fun findPlayerByRecoveryCode(recoveryCode: RecoveryCode): Optional<Player>
}