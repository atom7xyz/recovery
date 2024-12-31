package xyz.atom7.recoveryweb

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.atom7.recoveryweb.controllers.CodeController
import xyz.atom7.recoveryweb.controllers.PlayerController
import java.time.LocalDateTime
import kotlin.test.assertNotNull

@SpringBootTest
class RecoveryWebApplicationTests
{

    @Autowired
    private lateinit var playerController: PlayerController
    @Autowired
    private lateinit var codeController: CodeController

    @Test
    fun contextLoads()
    {
        assertNotNull(playerController)
        assertNotNull(codeController)
    }

}

fun clearLastZeroes(date: LocalDateTime): String
{
    var dateString = date.toString()

    while (dateString.endsWith("0"))
    {
        dateString = dateString.dropLast(1)
    }

    return dateString
}