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
import xyz.atom7.recoveryweb.entities.RecoveryCode
import xyz.atom7.recoveryweb.services.CodeService
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
class CodeControllerTest
{
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var codeService: CodeService

    @Test
    fun `should return a valid RecoveryCode when username is provided`()
    {
        val recoveryCode = RecoveryCode(
            id = 1,
            code = "1234",
            username = "testuser",
            expired = false,
            creationDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusDays(7)
        )
        given(codeService.createCode("testuser")).willReturn(recoveryCode)

        val creationDate = clearLastZeroes(recoveryCode.creationDate)
        val expirationDate = clearLastZeroes(recoveryCode.expirationDate)

        mockMvc.perform(
                get("/code/create")
                    .header("X-API-KEY", "changeme")
                    .param("username", recoveryCode.username)
            )
            .andExpect(status().isOk)
            .andExpect(content().json(
                """
                {
                    "id": ${recoveryCode.id},
                    "code": "${recoveryCode.code}",
                    "username": "${recoveryCode.username}",
                    "expired": ${recoveryCode.expired},
                    "creationDate": "$creationDate",
                    "expirationDate": "$expirationDate"
                }
                """.trimIndent()))
    }

    @Test
    fun `should return true if the username and code provided point to a valid (non-expired) entry`()
    {
        val recoveryCode = RecoveryCode(
            id = 1,
            code = "1234",
            username = "testuser",
            expired = false,
            creationDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusDays(7)
        )
        given(codeService.checkCode(recoveryCode.username, recoveryCode.code))
            .willReturn(mapOf("valid" to true))

        mockMvc.perform(
                get("/code/check")
                    .header("X-API-KEY", "changeme")
                    .param("username", recoveryCode.username)
                    .param("code", recoveryCode.code)
            )
            .andExpect(status().isOk)
            .andExpect(content().json(
                """
                {
                    "valid": ${!recoveryCode.expired}
                }
                """.trimIndent()))
    }

    @Test
    fun `should return false if the username and code provided point to a valid (non-expired) entry`()
    {
        val recoveryCode = RecoveryCode(
            id = 1,
            code = "1234",
            username = "testuser",
            expired = true,
            creationDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now()
        )
        given(codeService.checkCode(recoveryCode.username, recoveryCode.code))
            .willReturn(mapOf("valid" to false))

        mockMvc.perform(
            get("/code/check")
                .header("X-API-KEY", "changeme")
                .param("username", recoveryCode.username)
                .param("code", recoveryCode.code)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(
                """
                {
                    "valid": ${!recoveryCode.expired}
                }
                """.trimIndent()))
    }

    @Test
    fun `should return true when the code is successfully set as expired (deleted)`()
    {
        val recoveryCode = RecoveryCode(
            id = 1,
            code = "1234",
            username = "testuser",
            expired = false,
            creationDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusDays(7)
        )

        given(codeService.deleteCode(recoveryCode.username, recoveryCode.code))
            .willReturn(mapOf("deleted" to true))

        mockMvc.perform(
                get("/code/delete")
                    .header("X-API-KEY", "changeme")
                    .param("username", recoveryCode.username)
                    .param("code", recoveryCode.code)
            )
            .andExpect(status().isOk)
            .andExpect(content().json(
                """
                {
                    "deleted": true
                }
                """.trimIndent()))
    }

    @Test
    fun `should return false when the code is already expired (deleted)`()
    {
        val recoveryCode = RecoveryCode(
            id = 1,
            code = "1234",
            username = "testuser",
            expired = false,
            creationDate = LocalDateTime.now(),
            expirationDate = LocalDateTime.now().plusDays(7)
        )

        given(codeService.deleteCode(recoveryCode.username, recoveryCode.code))
            .willReturn(mapOf("deleted" to false))

        mockMvc.perform(
                get("/code/delete")
                    .header("X-API-KEY", "changeme")
                    .param("username", recoveryCode.username)
                    .param("code", recoveryCode.code)
            )
            .andExpect(status().isOk)
            .andExpect(content().json(
                """
                {
                    "deleted": false
                }
                """.trimIndent()))
    }

}
