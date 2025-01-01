package xyz.atom7.recoveryVelocity;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.event.connection.PreLoginEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import org.slf4j.Logger
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import xyz.atom7.recoveryVelocity.connectivity.PremiumRequest
import xyz.atom7.recoveryVelocity.utils.backendError
import xyz.atom7.recoveryVelocity.utils.connectionDenied
import xyz.sorridi.stone.common.data.structures.SoftCleaner
import java.nio.file.Files
import java.nio.file.Path

@Plugin(
    id = "recovery-velocity",
    name = "recovery-velocity",
    version = BuildConstants.VERSION,
    url = "https://atom7.xyz",
    authors = ["atom7xyz"],
    dependencies = [ Dependency(id = "stone-velocity") ]
)

class RecoveryVelocity @Inject constructor(val logger: Logger)
{
    private lateinit var premiumRequest: PremiumRequest

    private val dataDirectory: Path = Path.of("./plugins/recovery-velocity")

    @Subscribe
    fun on(event: ProxyInitializeEvent)
    {
        try
        {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory)
            }

            val config: Path = dataDirectory.resolve("config.yml")

            if (Files.notExists(config)) {
                javaClass.classLoader.getResourceAsStream("config.yml")?.use { stream ->
                    Files.copy(stream, config)
                } ?: throw IllegalStateException("Default config.yml not found in resources.")
            }

            val loader: YamlConfigurationLoader = YamlConfigurationLoader.builder().path(config).build()
            val root: CommentedConfigurationNode = loader.load()

            premiumRequest = PremiumRequest(root)
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            throw ex
        }

        SoftCleaner.setLogging(false)
    }

    @Subscribe
    fun on(event: ProxyShutdownEvent)
    {
        premiumRequest.shutdownPool()
    }

    @Subscribe
    fun on(event: PreLoginEvent)
    {
        val virtualHost = event.connection.virtualHost

        if (virtualHost.isEmpty) {
            event.result = PreLoginEvent.PreLoginComponentResult.denied(backendError)
            return
        }

        val domain = virtualHost.get().hostName

        println("logging in from: $domain")

        event.result = when (domain)
        {
            premiumRequest.premiumDomain -> PreLoginEvent.PreLoginComponentResult.forceOnlineMode()
            premiumRequest.crackedDomain -> PreLoginEvent.PreLoginComponentResult.forceOfflineMode()
            else -> PreLoginEvent.PreLoginComponentResult.denied(connectionDenied)
        }
    }

    @Subscribe
    fun on(event: LoginEvent)
    {
        println("LoginEvent: ${event.player.username} is premium=${event.player.isOnlineMode}")

        val player = event.player
        premiumRequest.sendPremiumPlayer(player.username, player.isOnlineMode)
    }

    @Subscribe
    fun on(event: DisconnectEvent)
    {
        println("DisconnectEvent: ${event.player.username} is premium=${event.player.isOnlineMode}")

        val player = event.player
        premiumRequest.removePremiumPlayer(player.username)
    }

}
