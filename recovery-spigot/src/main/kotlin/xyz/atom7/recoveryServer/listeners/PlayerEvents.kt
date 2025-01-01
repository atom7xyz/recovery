package xyz.atom7.recoveryServer.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.TerminableConsumer
import me.lucko.helper.terminable.composite.CompositeTerminable
import me.lucko.helper.terminable.module.TerminableModule
import net.kyori.adventure.text.TextReplacementConfig
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.atom7.recoveryServer.providers.player.*
import xyz.atom7.recoveryServer.utils.*
import java.util.concurrent.TimeUnit

class PlayerEvents(private val plugin: Plugin) : TerminableModule
{

    override fun setup(consumer: TerminableConsumer)
    {
        Events
            .subscribe(AsyncChatEvent::class.java)
            .handler { it.isCancelled = true }
            .bindWith(consumer)

        Events
            .subscribe(PlayerQuitEvent::class.java)
            .handler {
                val name = it.player.name

                // cleanup providers
                VerifiedProvider.remove(name)
                TerminableProvider.remove(name)
                FriendlyExpProvider.remove(name)

                Schedulers.builder()
                    .sync()
                    .after(1, TimeUnit.DAYS)
                    .run {
                        TriesProvider.remove(name)
                    }

                it.quitMessage(emptyTextComponent)
            }
            .bindWith(consumer)

        Events
            .subscribe(AsyncPlayerPreLoginEvent::class.java)
            .handler { e ->
                val name = e.playerProfile.name ?: return@handler

                if (!JoinDelayProvider.isUsable(name)) {
                    e.kickMessage(JoinDelayProvider.getBanMessage(name))
                    e.loginResult = AsyncPlayerPreLoginEvent.Result.KICK_OTHER
                }
            }
            .bindWith(consumer)

        Events
            .subscribe(PlayerJoinEvent::class.java)
            .handler {
                val name = it.player.name

                // init providers
                TerminableProvider.set(name, CompositeTerminable.create())
                TriesProvider.add(name, 3)
                JoinDelayProvider.remove(name)

                // hide other players from the player
                plugin.server.onlinePlayers.forEach { player ->
                    it.player.hidePlayer(plugin, player)
                    player.hidePlayer(plugin, it.player)
                }

                // apply night vision
                it.player.clearActivePotionEffects()
                it.player.addPotionEffect(nightVisionPotion)

                handleJoin(it.player)
                it.joinMessage(emptyTextComponent)
            }
            .bindWith(consumer)
    }

    private fun handleJoin(player: Player)
    {
        val replaceName = TextReplacementConfig.builder()
            .matchLiteral("<nome>")
            .replacement(player.name)
            .build()
        val sayHi = sayHi.replaceText(replaceName)

        val consumer = TerminableProvider.get(player.name)!!

        Schedulers.builder()
            .sync()
            .after(2, TimeUnit.SECONDS)
            .run {

                // says hi
                player.sendMessage(sayHi)
                player.playSound(soundHi)

            }
            .thenRunDelayedSync ({

                // introduces the thing
                player.sendMessage(sayIntroduction)
                player.playSound(soundExplain)

            }, 2, TimeUnit.SECONDS)
            .thenRunDelayedSync ({

                Schedulers.builder()
                    .sync()
                    .every(10, TimeUnit.SECONDS)
                    .run {
                        val name = player.name

                        if (VerifiedProvider.contains(name)) {
                            consumer.close()
                            return@run
                        }

                        if (FriendlyExpProvider.contains(name)) {
                            FriendlyExpProvider.remove(name)
                            return@run
                        }

                        clearPlayerChat(player)
                        player.sendMessage(sayVerify)
                        player.playSound(soundExplain)
                    }
                    .bindWith(consumer)

            }, 4, TimeUnit.SECONDS)
            .bindWith(consumer)
    }

    private val nightVisionPotion = PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1)

}
