package xyz.atom7.recoveryweb.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object AppLogger
{
    private val logger: Logger = LoggerFactory.getLogger(AppLogger::class.java)

    private const val DIV_MESSAGE: String = "########################################"

    fun print(logLevel: LogLevel, message: String)
    {
        when (logLevel)
        {
            LogLevel.INFO -> logger.info(message)
            LogLevel.WARN -> logger.warn(message)
            LogLevel.ERROR -> logger.error(message)
        }
    }

    fun div(logLevel: LogLevel)
    {
        when (logLevel)
        {
            LogLevel.INFO -> print(LogLevel.INFO, DIV_MESSAGE)
            LogLevel.WARN -> print(LogLevel.WARN, DIV_MESSAGE)
            LogLevel.ERROR -> print(LogLevel.ERROR, DIV_MESSAGE)
        }
    }
}
