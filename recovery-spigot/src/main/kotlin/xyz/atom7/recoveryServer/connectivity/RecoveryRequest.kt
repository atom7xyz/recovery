package xyz.atom7.recoveryServer.connectivity

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bukkit.configuration.Configuration
import xyz.atom7.recoveryServer.exceptions.RecoveryRequestException
import xyz.atom7.recoveryServer.providers.service.GsonProvider
import xyz.atom7.recoveryServer.serialization.Player
import xyz.atom7.recoveryServer.serialization.RecoveryCode
import xyz.atom7.recoveryServer.serialization.SerialException
import xyz.atom7.recoveryServer.serialization.ValidCode
import xyz.sorridi.stone.common.threading.Pool
import java.util.concurrent.CompletableFuture

class RecoveryRequest(
    config: Configuration
) {

    private val pool: Pool = Pool(1)
    private val client: OkHttpClient = OkHttpClient()

    private val scheme = config.getString("web.scheme")!!
    private val host = config.getString("web.host")!!
    private val port = config.getInt("web.port")
    private val apiKey = config.getString("web.api-key")!!

    fun shutdownPool()
    {
        pool.shutdown()
    }

    private fun <T> send(httpUrl: HttpUrl, parser: (String) -> T): CompletableFuture<T>
    {
        val request = Request.Builder()
            .url(httpUrl)
            .addHeader("X-API-KEY", apiKey)
            .build()

        return CompletableFuture.supplyAsync({
            println("sending req from thread=${Thread.currentThread().name}")

            client.newCall(request).execute().use { response ->

                println("req sent for httpUrl=$httpUrl")

                val strResponse = response.body?.string()
                println("Raw Response Body: $strResponse")

                if (!response.isSuccessful) {
                    val exception = GsonProvider.gson.fromJson(strResponse, SerialException::class.java)
                    throw RecoveryRequestException(httpUrl, exception)
                }

                if (strResponse.isNullOrEmpty()) {
                    throw RuntimeException("Response from $httpUrl is empty or null!")
                }

                parser(strResponse)
            }
        }, pool.executor).exceptionally { throwable ->
            println("CompletableFuture completed exceptionally: ${throwable.message}")
            throw throwable
        }
    }

    fun sendCreatePlayer(username: String,
                         address: String,
                         premium: Boolean,
                         clientVersion: String,
                         codeUsed: Int) : CompletableFuture<Player>
    {
        val httpUrl = HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .port(port)
            .addPathSegments("player/create")
            .addQueryParameter("username", username)
            .addQueryParameter("address", address)
            .addQueryParameter("premium", premium.toString())
            .addQueryParameter("clientVersion", clientVersion)
            .addQueryParameter("codeUsed", codeUsed.toString())
            .build()

        return send(httpUrl) { response ->
            GsonProvider.gson.fromJson(response, Player::class.java)
        }
    }

    fun sendCheckCode(username: String, codeUsed: Int): CompletableFuture<ValidCode>
    {
        val httpUrl = HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .port(port)
            .addPathSegments("code/check")
            .addQueryParameter("username", username)
            .addQueryParameter("code", codeUsed.toString())
            .build()

        return send(httpUrl) { response ->
            GsonProvider.gson.fromJson(response, ValidCode::class.java)
        }
    }

}
