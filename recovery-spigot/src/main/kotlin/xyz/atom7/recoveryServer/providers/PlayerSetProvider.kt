package xyz.atom7.recoveryServer.providers

interface PlayerSetProvider<K>
{
    val set: MutableSet<K>

    fun add(player: K)

    fun remove(player: K)

    fun contains(player: K): Boolean
}