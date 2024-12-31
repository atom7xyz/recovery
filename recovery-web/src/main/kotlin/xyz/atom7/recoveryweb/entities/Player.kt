package xyz.atom7.recoveryweb.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Player(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val username: String,

    val address: String,

    val premium: Boolean,

    val clientVersion: String,

    val loginTime: LocalDateTime = LocalDateTime.now(),

    @OneToOne
    val recoveryCode: RecoveryCode

) {

    override fun toString(): String {
        return "Player(" +
                "id=$id, " +
                "username='$username', " +
                "address='$address', " +
                "premium=$premium, " +
                "clientVersion='$clientVersion', " +
                "loginTime=$loginTime, " +
                "recoveryCode='$recoveryCode'" +
                ")"
    }

}