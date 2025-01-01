package xyz.atom7.recoveryweb.controllers

import org.springframework.web.bind.annotation.*
import xyz.atom7.recoveryweb.entities.Player
import xyz.atom7.recoveryweb.services.PlayerService

@RestController
@RequestMapping("/player")
class PlayerController(val playerService: PlayerService)
{

    @PostMapping("/create")
    fun createPlayer(@RequestParam username: String,
                     @RequestParam address: String,
                     @RequestParam premium: Boolean,
                     @RequestParam clientVersion: String,
                     @RequestParam codeUsed: String): Player
    {
        return playerService.createPlayer(username, address, premium, clientVersion, codeUsed)
    }

    @GetMapping("/history")
    fun historyPlayer(@RequestParam username: String,
                      @RequestParam reversed: Boolean?,
                      @RequestParam last: Boolean?): List<Player>
    {
        // check if it's requested the last entry of the player
        if (last == true) {
            return listOf(playerService.lastHistoryPlayer(username))
        }

        val history = playerService.historyPlayer(username)

        if (reversed == true) {
            return history.reversed()
        }

        return history
    }

    @PutMapping("/premium/set")
    fun premiumSetPlayer(@RequestParam username: String, @RequestParam premium: Boolean): String
    {
        playerService.setPremiumPlayer(username, premium)
        return "OK"
    }

    @GetMapping("/premium/check")
    fun premiumCheckPlayer(@RequestParam username: String): Boolean
    {
        return playerService.isPremiumPlayer(username)
    }

    @DeleteMapping("/premium/remove")
    fun premiumRemovePlayer(@RequestParam username: String): String
    {
        playerService.removePremiumPlayer(username)
        return "OK"
    }

    @GetMapping("/count")
    fun countPlayers(): Long
    {
        return playerService.countPlayers()
    }
}
