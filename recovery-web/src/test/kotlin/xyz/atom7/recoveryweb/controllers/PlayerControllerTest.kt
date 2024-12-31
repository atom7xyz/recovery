package xyz.atom7.recoveryweb.controllers

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import xyz.atom7.recoveryweb.clearLastZeroes
import xyz.atom7.recoveryweb.entities.Player
import xyz.atom7.recoveryweb.entities.RecoveryCode
import xyz.atom7.recoveryweb.services.CodeService
import xyz.atom7.recoveryweb.services.PlayerService
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerTest
{
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var playerService: PlayerService
    @MockBean
    private lateinit var codeService: CodeService

    @Test
    fun `should create a new entry Player`()
    {
        val recoveryCode = RecoveryCode(
            id = 1,
            code = "1234",
            username = "testuser",
            expired = false,
            creationDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusDays(7)
        )

        val player = Player(
            id = 1,
            username = "testuser",
            address = "127.0.0.1",
            premium = true,
            clientVersion = "1.7.10",
            loginTime = LocalDateTime.now(),
            recoveryCode = recoveryCode
        )

        given(codeService.createCode("testuser")).willReturn(recoveryCode)

        given(
            playerService.createPlayer(
                player.username,
                player.address,
                player.premium,
                player.clientVersion,
                player.recoveryCode.code
            )
        ).willReturn(player)

        val loginTime = clearLastZeroes(player.loginTime)
        val creationDate = clearLastZeroes(recoveryCode.creationDate)
        val expirationDate = clearLastZeroes(recoveryCode.expirationDate)

        mockMvc.perform(
            get("/player/create")
                .header("X-API-KEY", "changeme")
                .param("username", player.username)
                .param("address", player.address)
                .param("premium", player.premium.toString())
                .param("clientVersion", player.clientVersion)
                .param("codeUsed", player.recoveryCode.code)
            )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                """
                {
                    "id": ${player.id},
                    "username": "${player.username}",
                    "address": "${player.address}",
                    "premium": ${player.premium},
                    "clientVersion": "${player.clientVersion}",
                    "loginTime": "$loginTime",
                    "recoveryCode": {
                        "id": ${recoveryCode.id},
                        "code": "${recoveryCode.code}",
                        "username": "${recoveryCode.username}",
                        "expired": ${recoveryCode.expired},
                        "creationDate": "$creationDate",
                        "expirationDate": "$expirationDate"
                    }
                }
                """.trimIndent()))
    }

    @Test
    fun `should return the history of Player entries of a username`()
    {
        val recoveryCode = RecoveryCode(
            id = 1,
            code = "1234",
            username = "testuser",
            expired = false,
            creationDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusDays(7)
        )

        val player = Player(
            id = 1,
            username = "testuser",
            address = "127.0.0.1",
            premium = true,
            clientVersion = "1.7.10",
            loginTime = LocalDateTime.now(),
            recoveryCode = recoveryCode
        )

        given(playerService.historyPlayer(player.username)).willReturn(mutableListOf(player))

        val loginTime = clearLastZeroes(player.loginTime)
        val creationDate = clearLastZeroes(recoveryCode.creationDate)
        val expirationDate = clearLastZeroes(recoveryCode.expirationDate)

        mockMvc.perform(
            get("/player/history")
                .header("X-API-KEY", "changeme")
                .param("username", player.username)
            )
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                """
                [
                    {
                        "id": ${player.id},
                        "username": "${player.username}",
                        "address": "${player.address}",
                        "premium": ${player.premium},
                        "clientVersion": "${player.clientVersion}",
                        "loginTime": "$loginTime",
                        "recoveryCode": {
                            "id": ${recoveryCode.id},
                            "code": "${recoveryCode.code}",
                            "username": "${recoveryCode.username}",
                            "expired": ${recoveryCode.expired},
                            "creationDate": "$creationDate",
                            "expirationDate": "$expirationDate"
                        }
                    }
                ]
                """.trimIndent()))
    }

}