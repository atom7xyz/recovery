package xyz.atom7.recoveryVelocity.providers

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonProvider
{
    val gson: Gson = GsonBuilder().create()
}
