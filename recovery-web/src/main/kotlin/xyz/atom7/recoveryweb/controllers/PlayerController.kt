package xyz.atom7.recoveryweb.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.atom7.recoveryweb.entities.Player
import xyz.atom7.recoveryweb.services.PlayerService

@RestController
@RequestMapping("/player")
class PlayerController(val playerService: PlayerService)
{

    @GetMapping("/create")
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

}
