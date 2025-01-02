package xyz.atom7.recoveryweb.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import xyz.atom7.recoveryweb.logging.RateLimitType
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@Service
@EnableScheduling
class RateLimiterService(
    @Value("\${app.rate-limit-action}")
    val rateLimitAction: Int,

    @Value("\${app.rate-limit-wrong-api-key}")
    val rateLimitWrongApiKey: Int
)
{
    private val actionRequests: MutableMap<String, AtomicInteger> = mutableMapOf()
    private val wrongApiKeyRequests: MutableMap<String, AtomicInteger> = mutableMapOf()

    fun hasReachedRateLimit(address: String, rateLimitType: RateLimitType): Boolean
    {
        return when (rateLimitType)
        {
            RateLimitType.ACTION -> checkRateLimit(address, rateLimitAction, actionRequests)
            RateLimitType.API_KEY -> checkRateLimit(address, rateLimitWrongApiKey, wrongApiKeyRequests)
        }
    }

    fun signHit(address: String, rateLimitType: RateLimitType)
    {
        return when (rateLimitType)
        {
            RateLimitType.ACTION -> incrEntry(address, actionRequests)
            RateLimitType.API_KEY -> incrEntry(address, wrongApiKeyRequests)
        }
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    fun resetCounts()
    {
        actionRequests.clear()
        wrongApiKeyRequests.clear()
    }

    private fun <T : MutableMap<String, AtomicInteger>> incrEntry(address: String, map: T)
    {
        map.putIfAbsent(address, AtomicInteger(0))
        map[address]!!.getAndIncrement()
    }

    private fun <T : MutableMap<String, AtomicInteger>> checkRateLimit(address: String, limit: Int, map: T): Boolean
    {
        map.putIfAbsent(address, AtomicInteger(1))
        return map[address]!!.get() > limit
    }
}