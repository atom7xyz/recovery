package xyz.atom7.recoveryServer.providers.player

import me.lucko.helper.terminable.composite.CompositeTerminable
import xyz.atom7.recoveryServer.providers.PlayerMapProvider

object TerminableProvider : PlayerMapProvider<String, CompositeTerminable>
{
    override val map: MutableMap<String, CompositeTerminable> by lazy {
        mutableMapOf()
    }

    override fun add(player: String, value: CompositeTerminable)
    {
        map.putIfAbsent(player, value)
    }

    override fun remove(player: String)
    {
        map.remove(player)
    }

    override fun contains(player: String): Boolean
    {
        return map.containsKey(player)
    }

    override fun get(player: String): CompositeTerminable?
    {
        return map[player]
    }

    override fun set(player: String, value: CompositeTerminable)
    {
        map[player] = value
    }
}
