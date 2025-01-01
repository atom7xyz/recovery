package xyz.atom7.recoveryweb.controllers

import org.springframework.web.bind.annotation.*
import xyz.atom7.recoveryweb.entities.RecoveryCode
import xyz.atom7.recoveryweb.services.CodeService

@RestController
@RequestMapping("/code")
class CodeController(val codeService: CodeService)
{

    @PostMapping("/create")
    fun createCode(@RequestParam username: String): RecoveryCode
    {
        return codeService.createCode(username)
    }

    @GetMapping("/check")
    fun checkCode(@RequestParam username: String, @RequestParam code: String): Map<String, Boolean>
    {
        return codeService.checkCode(username, code)
    }

    @DeleteMapping("/delete")
    fun deleteCode(@RequestParam username: String, @RequestParam code: String): Map<String, Boolean>
    {
        return codeService.deleteCode(username, code)
    }

    @GetMapping("/list")
    fun listCodes(): MutableList<RecoveryCode>
    {
        return codeService.listAllCodes()
    }

    @GetMapping("/count")
    fun countCodes(): Long
    {
        return codeService.countCodes()
    }
}
