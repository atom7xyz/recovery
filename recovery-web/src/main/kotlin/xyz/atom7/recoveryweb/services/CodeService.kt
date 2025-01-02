package xyz.atom7.recoveryweb.services

import org.springframework.stereotype.Service
import xyz.atom7.recoveryweb.entities.RecoveryCode
import xyz.atom7.recoveryweb.exceptions.types.CodeExistsException
import xyz.atom7.recoveryweb.exceptions.types.RecoveryCodeNotFound
import xyz.atom7.recoveryweb.repositories.CodeRepository
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class CodeService(val codeRepository: CodeRepository)
{

    @Throws(CodeExistsException::class)
    fun createCode(username: String): RecoveryCode
    {
        val codeFound = codeRepository.findRecoveryCodeByUsernameAndExpired(username, false)

        codeFound.ifPresent {
            if (!checkExpiration(it)) {
                throw CodeExistsException(username, it)
            }
        }

        val code = Random.nextInt(1000, 9999).toString()

        val recoveryCode = RecoveryCode(
            username = username,
            code = code
        )
        codeRepository.save(recoveryCode)

        return recoveryCode
    }

    fun checkCode(username: String, code: String): Map<String, Boolean>
    {
        val codeFound = codeRepository.findRecoveryCodeByUsernameAndCodeAndExpired(username, code, false)

        codeFound.ifPresent {
            checkExpiration(it)
        }

        return mapOf("valid" to (codeFound.isPresent && !codeFound.get().expired))
    }

    @Throws(RecoveryCodeNotFound::class)
    fun deleteCode(username: String, code: String): Map<String, Boolean>
    {
        val codeFound = codeRepository.findRecoveryCodeByUsernameAndCodeAndExpired(username, code, false)

        if (codeFound.isEmpty) {
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

    fun countCodes(): Long
    {
        return codeRepository.count()
    }

    /**
     * Checks if the given recovery code is expired and updates its state if necessary.
     *
     * @param code The [RecoveryCode] to evaluate.
     * @return `true` if the recovery code is expired, `false` otherwise.
     *
     * This function marks the code as expired and saves it to the repository if the expiration date
     * is before or equal to the current time.
     */
    private fun checkExpiration(code: RecoveryCode): Boolean
    {
        val now = LocalDateTime.now()

        val expiredTimeAgo = code.expirationDate.isBefore(now)
        val expiredNow = code.expirationDate.isEqual(now)

        if (expiredTimeAgo || expiredNow) {
            code.expired = true
            codeRepository.save(code)
        }

        return expiredTimeAgo || expiredNow
    }

}
