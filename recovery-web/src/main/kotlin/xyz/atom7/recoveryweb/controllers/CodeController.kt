package xyz.atom7.recoveryweb.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.atom7.recoveryweb.entities.RecoveryCode
import xyz.atom7.recoveryweb.services.CodeService

@RestController
@RequestMapping("/code")
class CodeController(val codeService: CodeService)
{

    @GetMapping("/create")
    fun createCode(@RequestParam username: String): RecoveryCode
    {
        return codeService.createCode(username)
    }

    @GetMapping("/check")
    fun checkCode(@RequestParam username: String, @RequestParam code: String): Map<String, Boolean>
    {
        return codeService.checkCode(username, code)
    }

    @GetMapping("/delete")
    fun deleteCode(@RequestParam username: String, @RequestParam code: String): Map<String, Boolean>
    {
        return codeService.deleteCode(username, code)
    }

    @GetMapping("/list")
    fun listCodes(): MutableList<RecoveryCode>
    {
        return codeService.listAllCodes()
    }

}
