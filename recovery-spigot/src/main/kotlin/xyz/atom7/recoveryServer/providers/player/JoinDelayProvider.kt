package xyz.atom7.recoveryServer.providers.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.NamedTextColor
import xyz.atom7.recoveryServer.providers.PlayerMapProvider
import xyz.atom7.recoveryServer.providers.PlayerSetProvider
import xyz.sorridi.stone.common.builders.UseCoolDown
import java.util.concurrent.TimeUnit

object JoinDelayProvider : PlayerMapProvider<String, Long>
{
    override val map: UseCoolDown<String> by lazy {
        UseCoolDown(10, TimeUnit.MINUTES)
    }

    fun add(player: String)
    {
        map.create(player)
        map.refresh(player)
    }

    @Deprecated("use add(String)",
        ReplaceWith("add(player)", "xyz.atom7.recoveryServer.providers.player.JoinDelayProvider.add")
    )
    override fun add(player: String, value: Long)
    {
        add(player)
    }

    override fun set(player: String, value: Long)
    {
        map[player] = value
    }

    override fun remove(player: String)
    {
        map.remove(player)
    }

    override fun contains(player: String): Boolean
    {
        return map.containsKey(player)
    }

    override fun get(player: String): Long?
    {
        return map[player]
    }

    fun isUsable(player: String): Boolean
    {
        if (!contains(player)) {
            return true
        }

        return map.isUsable(player)
    }

    fun getBanMessage(player: String): TextComponent
    {
        val usableInSeconds = map.usableIn(player, TimeUnit.SECONDS)

        val usableInMinutesPart = usableInSeconds / 60
        val usableInSecondsPart = usableInSeconds % 60

        val replacement = TextReplacementConfig.builder()
            .matchLiteral("<time>")
            .replacement("${usableInMinutesPart}m${usableInSecondsPart}s")
            .build()

        return message.replaceText(replacement) as TextComponent
    }

    private val message = Component.text()
        .append(
            Component.text("Per ragioni di sicurezza, potrai rientrare solo tra: <time>", NamedTextColor.RED)
        )
        .build()
}
