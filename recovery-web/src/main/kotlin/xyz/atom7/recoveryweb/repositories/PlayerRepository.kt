package xyz.atom7.recoveryweb.repositories

import org.springframework.data.jpa.repository.JpaRepository
import xyz.atom7.recoveryweb.entities.Player

interface PlayerRepository : JpaRepository<Player, Long>
{
    fun findPlayerByUsername(username: String): MutableList<Player>
    fun findPlayerByUsernameOrderByIdAsc(username: String): MutableList<Player>
}
