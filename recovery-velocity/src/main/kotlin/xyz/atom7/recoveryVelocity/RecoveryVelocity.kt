package xyz.atom7.recoveryVelocity;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import org.slf4j.Logger

@Plugin(
    id = "recovery-velocity",
    name = "recovery-velocity",
    version = BuildConstants.VERSION,
    url = "https://atom7.xyz",
    authors = ["atom7xyz"]
)
class RecoveryVelocity @Inject constructor(val logger: Logger) {

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent)
    {

    }
}
