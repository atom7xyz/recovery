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
    fun historyPlayer(@RequestParam username: String): MutableList<Player>
    {
        return playerService.historyPlayer(username)
    }

    @PostMapping("/premium/set")
    fun premiumSetPlayer(@RequestParam username: String, @RequestParam premium: Boolean)
    {
        playerService.setPremiumPlayer(username, premium)
    }

    @GetMapping("/premium/check")
    fun premiumCheckPlayer(@RequestParam username: String): Boolean
    {
        return playerService.isPremiumPlayer(username)
    }

    @PostMapping("/premium/remove")
    fun premiumRemovePlayer(@RequestParam username: String)
    {
        playerService.removePremiumPlayer(username)
    }

}
