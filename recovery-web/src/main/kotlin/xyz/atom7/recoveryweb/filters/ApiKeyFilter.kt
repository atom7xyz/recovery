package xyz.atom7.recoveryweb.filters

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import xyz.atom7.recoveryweb.logging.AppLogger.div
import xyz.atom7.recoveryweb.logging.AppLogger.print
import xyz.atom7.recoveryweb.logging.LogLevel

@Component
class ApiKeyFilter(
    @Value("\${app.api-key}")
    private val validApiKey: String
) : Filter
{
    private val HEADER: String = "X-API-KEY"
    private val HEADER_DEFAULT: String = "changeme"

    init
    {
        if (validApiKey == HEADER_DEFAULT) {
            div(LogLevel.WARN)
            print(LogLevel.WARN, "API Key has default value!")
            print(LogLevel.WARN, "For security reasons, it is recommended to set a unique API key in the config: `app.api-key`")
            div(LogLevel.WARN)
        }
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain)
    {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val apiKey = httpRequest.getHeader(HEADER)

        if (apiKey == validApiKey) {
            chain.doFilter(request, response)
            return
        }

        httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
        httpResponse.writer.write("Invalid or missing API key")
    }

}
