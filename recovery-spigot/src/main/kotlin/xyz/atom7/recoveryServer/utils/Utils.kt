package xyz.atom7.recoveryServer.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

fun clearPlayerChat(player: Player)
{
    var i = 0
    while (i++ != 25) {
        player.sendMessage(" ")
    }
}

val soundLevelUp = Sound.sound()
    .type(Key.key("minecraft:entity.player.levelup"))
    .source(Sound.Source.MASTER)
    .build()

val soundCodeFailed = Sound.sound()
    .type(Key.key("minecraft:entity.wandering_trader.death"))
    .source(Sound.Source.MASTER)
    .build()

val soundHi = Sound.sound()
    .type(Key.key("minecraft:entity.wandering_trader.yes"))
    .source(Sound.Source.MASTER)
    .build()

val soundExplain = Sound.sound()
    .type(Key.key("minecraft:entity.wandering_trader.trade"))
    .source(Sound.Source.MASTER)
    .build()

val emptyTextComponent = Component.text().build()

val sayHi = Component.text()
    .appendNewline()
    .append(Component.text("Ciao <nome>!", NamedTextColor.GREEN))
    .build()

val sayIntroduction = Component.text()
    .appendNewline()
    .append(Component.text("Sei qui per verificare il tuo account... "))
    .append(Component.text("sei nel posto giusto!", NamedTextColor.GREEN))
    .build()

val sayVerify = Component.text()
    .appendNewline()
    .append(
        Component.text("Per ")
            .append(Component.text("completare la verifica", NamedTextColor.GREEN))
            .append(Component.text(", avrei bisogno che mi inviassi il "))
            .append(Component.text("codice", NamedTextColor.GREEN))
            .append(Component.text(" che ti è stato fornito "))
            .append(Component.text("nel ticket", NamedTextColor.GREEN))
            .append(Component.text(" su Discord."))
    )
    .appendNewline()
    .appendNewline()
    .append(Component.text("Utilizza il comando: /verifica <codice>", NamedTextColor.RED))
    .build()

val waitAdmin = Component.text()
    .append(
        Component.text("Attendi una risposta da parte di un Amministratore su Discord.", NamedTextColor.RED)
    )
    .build()

val verifySuccess = Component.text()
    .appendNewline()
    .append(Component.text("Verifica completata!", NamedTextColor.GREEN, TextDecoration.BOLD))
    .appendNewline()
    .append(Component.text("Il tuo ticket e' stato aggiornato."))
    .appendNewline()
    .append(waitAdmin)
    .build()

val completeCommand = Component.text()
    .appendNewline()
    .append(Component.text("Utilizza il comando: /verifica <codice>", NamedTextColor.RED))
    .appendNewline()
    .append(Component.text("Esempio: /verifica 1234", NamedTextColor.YELLOW))
    .build()

val invalidCode = Component.text()
    .appendNewline()
    .append(Component.text("Il codice che hai inserito non e' valido!", NamedTextColor.RED))
    .appendNewline()
    .append(
        Component.text("Hai ancora <num> tentativi per inserirlo correttamente!",
            NamedTextColor.RED, TextDecoration.BOLD)
    )
    .build()

val somethingWentWrong = Component.text()
    .append(
        Component.text("Si e' verificato un errore! Comunicalo agli Amministratori! :(", NamedTextColor.RED)
    )
    .build()