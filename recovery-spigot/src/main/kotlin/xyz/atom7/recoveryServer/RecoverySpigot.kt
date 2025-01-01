package xyz.atom7.recoveryServer

import me.lucko.helper.Commands
import me.lucko.helper.internal.HelperImplementationPlugin
import me.lucko.helper.plugin.ExtendedJavaPlugin
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import org.bukkit.GameRule
import org.bukkit.plugin.PluginLoadOrder
import xyz.atom7.recoveryServer.commands.VerifyCommand
import xyz.atom7.recoveryServer.commands.VersionCommand
import xyz.atom7.recoveryServer.connectivity.RecoveryRequest
import xyz.atom7.recoveryServer.listeners.PlayerEvents

@Plugin(
    name = "recovery-spigot",
    version = "1.0-SNAPSHOT-all",
    description = "Recovery server.",
    authors = ["atom7xyz"],
    website = "https://atom7.xyz",
    load = PluginLoadOrder.POSTWORLD,
    depends = [PluginDependency("helper"), PluginDependency("stone-spigot"), PluginDependency("ViaVersion")],
    apiVersion = "1.21"
)

@HelperImplementationPlugin
class RecoverySpigot : ExtendedJavaPlugin()
{
    lateinit var request: RecoveryRequest

    override fun enable()
    {
        request = RecoveryRequest(config)

        bindModule(PlayerEvents(this))

        Commands.create()
            .description("Recovery server status command.")
            .handler(VerifyCommand(this))
            .registerAndBind(this, "verifica")

        Commands.create()
            .description("Recovery server status command.")
            .handler(VersionCommand(this))
            .registerAndBind(this, "recovery")

        server.getWorld("world")?.let {
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            it.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        } ?: {
            throw RuntimeException("World `world` not found!")
        }

        // stone
        // SoftCleaner.setLogging(false)
    }

    override fun disable()
    {
        request.shutdownPool()
    }
}
