package xyz.atom7.recoveryweb.services

import org.springframework.stereotype.Service
import xyz.atom7.recoveryweb.entities.RecoveryCode
import xyz.atom7.recoveryweb.exceptions.types.CodeExistsException
import xyz.atom7.recoveryweb.exceptions.types.RecoveryCodeNotFound
import xyz.atom7.recoveryweb.repositories.CodeRepository
import kotlin.random.Random

@Service
class CodeService(val codeRepository: CodeRepository)
{
    @Throws(CodeExistsException::class)
    fun createCode(username: String): RecoveryCode
    {
        val codeFound = codeRepository.findRecoveryCodeByUsernameAndExpired(username, false)
        var recoveryCode: RecoveryCode

        codeFound.ifPresent {
            recoveryCode = codeFound.get()
            throw CodeExistsException(username, recoveryCode)
        }

        val code = Random.nextInt(1000, 9999).toString()

        recoveryCode = RecoveryCode(
            username = username,
            code = code
        )

        codeRepository.save(recoveryCode)

        return recoveryCode
    }

    @Throws(RecoveryCodeNotFound::class)
    fun checkCode(username: String, code: String): Map<String, Boolean>
    {
        val codeFound = codeRepository.findRecoveryCodeByUsernameAndCode(username, code)

        if (!codeFound.isPresent) {
            throw RecoveryCodeNotFound(username, code)
        }

        return mapOf("valid" to !codeFound.get().expired)
    }

    @Throws(RecoveryCodeNotFound::class)
    fun deleteCode(username: String, code: String): Map<String, Boolean>
    {
        val codeFound = codeRepository.findRecoveryCodeByUsernameAndCodeAndExpired(username, code, false)

        if (!codeFound.isPresent) {
            throw RecoveryCodeNotFound(username, code)
        }

        val recoveryCode = codeFound.get()
        recoveryCode.expireNow()
        codeRepository.save(recoveryCode)

        return mapOf("deleted" to true)
    }

    fun listAllCodes(): MutableList<RecoveryCode>
    {
        return codeRepository.findAll()
    }

}
