package xyz.atom7.recoveryVelocity.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

val connectionDenied = Component.text()
    .append(Component.text("Non e' possibile continuare la connessione al server.", NamedTextColor.RED))
    .build()

val backendError = Component.text()
    .append(
        Component.text("Si e' verificato un errore! Contatta un Amministratore.", NamedTextColor.RED)
    )
    .build()
