package xyz.atom7.recoveryweb.services

import org.springframework.stereotype.Service
import xyz.atom7.recoveryweb.entities.Player
import xyz.atom7.recoveryweb.exceptions.types.RecoveryCodeNotFound
import xyz.atom7.recoveryweb.providers.PremiumStatusesProvider
import xyz.atom7.recoveryweb.repositories.CodeRepository
import xyz.atom7.recoveryweb.repositories.PlayerRepository

@Service
class PlayerService(val codeRepository: CodeRepository, val playerRepository: PlayerRepository)
{

    fun createPlayer(username: String,
                     address: String,
                     premium: Boolean,
                     clientVersion: String,
                     codeUsed: String): Player
    {
        val optRecoveryCode = codeRepository.findRecoveryCodeByUsernameAndCodeAndExpired(username, codeUsed, false)

        if (!optRecoveryCode.isPresent) {
            throw RecoveryCodeNotFound(username, codeUsed)
        }

        val recoveryCode = optRecoveryCode.get()

        val player = Player(
            username = username,
            address = address,
            premium = premium,
            clientVersion = clientVersion,
            recoveryCode = recoveryCode
        )
        playerRepository.save(player)

        recoveryCode.expireNow()
        codeRepository.save(recoveryCode)

        return player
    }

    fun historyPlayer(username: String): MutableList<Player>
    {
        return playerRepository.findPlayerByUsername(username)
    }

    fun setPremiumPlayer(username: String, premium: Boolean)
    {
        PremiumStatusesProvider.set(username, premium)
    }

    fun isPremiumPlayer(username: String): Boolean
    {
        return PremiumStatusesProvider.isPremium(username) ?: false
    }

    fun removePremiumPlayer(username: String)
    {
        PremiumStatusesProvider.remove(username)
    }

}
