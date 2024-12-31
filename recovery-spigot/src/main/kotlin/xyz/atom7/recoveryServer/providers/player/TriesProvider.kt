package xyz.atom7.recoveryServer.providers.player

import xyz.atom7.recoveryServer.providers.PlayerMapProvider

object TriesProvider : PlayerMapProvider<String, Int>
{
    override val map: MutableMap<String, Int> by lazy {
        mutableMapOf()
    }

    override fun add(player: String, value: Int)
    {
        if (contains(player)) {
            return
        }

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

    override fun get(player: String): Int?
    {
        return map[player]
    }

    override fun set(player: String, value: Int)
    {
        map[player] = value
    }

    fun dec(player: String): Int
    {
        val left = get(player)!!

        if (left > 0) {
            set(player, left.dec())
        }
        else {
            remove(player)
        }

        return left
    }
}
