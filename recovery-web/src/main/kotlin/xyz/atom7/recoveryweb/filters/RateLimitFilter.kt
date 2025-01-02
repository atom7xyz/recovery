package xyz.atom7.recoveryweb.filters

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import xyz.atom7.recoveryweb.logging.RateLimitType
import xyz.atom7.recoveryweb.services.RateLimiterService

@Component
@Order(0)
class RateLimitFilter(private val rateLimiterService: RateLimiterService) : Filter
{

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain)
    {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val address = httpRequest.remoteAddr

        rateLimiterService.signHit(address, RateLimitType.ACTION)

        val actionLimitExceeded = rateLimiterService.hasReachedRateLimit(address, RateLimitType.ACTION)
        val wrongApiKeyLimitExceeded = rateLimiterService.hasReachedRateLimit(address, RateLimitType.API_KEY)

        if (!actionLimitExceeded && !wrongApiKeyLimitExceeded) {
            chain.doFilter(request, response)
            return
        }

        httpResponse.status = HttpStatus.TOO_MANY_REQUESTS.value()
        httpResponse.writer.write("Rate limit exceeded. Try again later.")
    }

}
