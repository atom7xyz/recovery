package xyz.atom7.recoveryServer.commands

import com.google.common.collect.ImmutableList
import com.viaversion.viaversion.api.Via
import com.viaversion.viaversion.api.ViaAPI
import me.lucko.helper.Schedulers
import me.lucko.helper.command.context.CommandContext
import me.lucko.helper.command.functional.FunctionalCommandHandler
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import xyz.atom7.recoveryServer.RecoveryServer
import xyz.atom7.recoveryServer.exceptions.InvalidCodeException
import xyz.atom7.recoveryServer.exceptions.RecoveryRequestException
import xyz.atom7.recoveryServer.providers.player.FriendlyExpProvider
import xyz.atom7.recoveryServer.providers.player.JoinDelayProvider
import xyz.atom7.recoveryServer.providers.player.TriesProvider
import xyz.atom7.recoveryServer.providers.player.VerifiedProvider
import xyz.atom7.recoveryServer.utils.*
import xyz.sorridi.stone.common.commands.StoneCommand
import xyz.sorridi.stone.spigot.utils.PluginVersion
import java.util.concurrent.TimeUnit

class VerifyCommand(plugin: RecoveryServer) : FunctionalCommandHandler<CommandSender>
{
    private val requester = plugin.request

    override fun handle(c: CommandContext<CommandSender>)
    {
        val sender = c.sender()
        val args: ImmutableList<String> = c.args()

        if (sender is ConsoleCommandSender) {
            sender.sendMessage("Player only command!")
            return
        }

        val player = sender as Player
        val name = player.name

        if (args.size != 1) {
            sendFailedMessage(player)
            return
        }

        val strCode = c.arg(0).value().get()
        val code = strCode.toIntOrNull() ?: run {
            sendFailedMessage(player)
            return
        }

        requester.sendCheckCode(name, code)
            .thenApplyAsync ({

                // check if the code is valid (response={ "valid": <boolean> })
                if (!it.valid) {
                    throw InvalidCodeException()
                }

                requester.sendCreatePlayer(
                    username = name,
                    address = player.address.address.toString(), // todo check if it's correct
                    premium = false, // todo check correctly for premium
                    clientVersion = Via.getAPI().getPlayerVersion(player).toString(), // todo convert
                    codeUsed = code
                )

                // code is valid, add the player to the VerifiedProvider so he does not get any reminder messages
                VerifiedProvider.add(player.name)

                clearPlayerChat(player)
                player.sendMessage(verifySuccess)
                player.playSound(soundLevelUp)

                // after 10 seconds, kick the player
                Schedulers.builder()
                    .sync()
                    .after(10, TimeUnit.SECONDS)
                    .run {
                        player.kick(waitAdmin)
                    }

            }, Schedulers.sync())
            .exceptionallyAsync({ throwable ->

                val cause = throwable.cause

                if (cause !is InvalidCodeException && cause !is RecoveryRequestException) {
                    cause?.printStackTrace()
                    player.sendMessage(somethingWentWrong)
                    return@exceptionallyAsync null
                }

                // get the tries left to insert a correct code
                val left = TriesProvider.dec(name)

                // as a security measure, kick the player if it has no more tries left
                if (left == 0) {
                    JoinDelayProvider.add(name)
                    player.kick(JoinDelayProvider.getBanMessage(name))
                    return@exceptionallyAsync null
                }

                sendInvalidCodeMessage(player, left)
                null
            }, Schedulers.sync())
    }

    private fun sendFailedMessage(player: Player)
    {
        FriendlyExpProvider.add(player.name)
        clearPlayerChat(player)
        player.sendMessage(completeCommand)
    }

    private fun sendInvalidCodeMessage(player: Player, left: Int)
    {
        FriendlyExpProvider.add(player.name)

        // build the message of invalid code
        val replacement = TextReplacementConfig.builder()
            .matchLiteral("<num>")
            .replacement("$left")
            .build()
        val invalidCode = invalidCode.replaceText(replacement)

        player.sendMessage(invalidCode)
        player.playSound(soundCodeFailed)
    }

}
