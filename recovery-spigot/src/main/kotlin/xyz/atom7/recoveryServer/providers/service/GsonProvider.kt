package xyz.atom7.recoveryServer.providers.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import xyz.atom7.recoveryServer.serialization.LocalDateTimeAdapter
import java.time.LocalDateTime

object GsonProvider
{
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()
}
