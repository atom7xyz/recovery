package xyz.atom7.recoveryServer.providers.player

import xyz.atom7.recoveryServer.providers.PlayerSetProvider

object VerifiedProvider : PlayerSetProvider<String>
{
    override val set: MutableSet<String> by lazy {
        mutableSetOf()
    }

    override fun add(player: String)
    {
        set.add(player)
    }

    override fun remove(player: String)
    {
        set.remove(player)
    }

    override fun contains(player: String): Boolean
    {
        return set.contains(player)
    }
}
