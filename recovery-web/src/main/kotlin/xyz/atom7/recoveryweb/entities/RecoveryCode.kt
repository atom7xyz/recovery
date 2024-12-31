package xyz.atom7.recoveryweb.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class RecoveryCode(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val code: String,

    val username: String,

    var expired: Boolean = false,

    val creationDate: LocalDateTime = LocalDateTime.now(),

    var expirationDate: LocalDateTime = LocalDateTime.now().plusWeeks(1)

) {

    fun expireNow(): RecoveryCode
    {
        expired = true
        expirationDate = LocalDateTime.now()
        return this
    }

    override fun toString(): String {
        return "RecoveryCode(" +
                "id=$id, " +
                "code='$code', " +
                "username='$username', " +
                "expired=$expired, " +
                "creationDate=$creationDate, " +
                "expirationDate=$expirationDate" +
                ")"
    }

}