package xyz.atom7.recoveryweb.providers

import java.util.*

object PremiumStatusesProvider
{
    private val map: MutableMap<String, Boolean> = Collections.synchronizedMap(mutableMapOf<String, Boolean>())

    fun put(username: String, premium: Boolean)
    {
        map.put(username, premium)
    }

    fun set(username: String, premium: Boolean)
    {
        map[username] = premium
    }

    fun get(username: String): Boolean?
    {
        return map[username]
    }

    fun isPremium(username: String): Boolean?
    {
        return get(username)
    }

    fun remove(username: String)
    {
        map.remove(username)
    }
}
