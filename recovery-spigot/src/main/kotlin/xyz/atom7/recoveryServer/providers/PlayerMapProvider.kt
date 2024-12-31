package xyz.atom7.recoveryServer.providers

interface PlayerMapProvider<K, V>
{
    val map: MutableMap<K, V>

    fun add(player: K, value: V)

    fun set(player: K, value: V)

    fun remove(player: K)

    fun contains(player: K): Boolean

    fun get(player: K): V?
}