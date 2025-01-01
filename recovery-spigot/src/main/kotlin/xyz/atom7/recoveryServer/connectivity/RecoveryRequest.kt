package xyz.atom7.recoveryServer.connectivity

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bukkit.configuration.Configuration
import xyz.atom7.recoveryServer.exceptions.RecoveryRequestException
import xyz.atom7.recoveryServer.providers.service.GsonProvider
import xyz.atom7.recoveryServer.serialization.Player
import xyz.atom7.recoveryServer.serialization.SerialException
import xyz.atom7.recoveryServer.serialization.ValidCode
import xyz.atom7.recoveryServer.utils.RequestType
import xyz.atom7.recoveryServer.utils.emptyRequestBody
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

    private fun <T> send(httpUrl: HttpUrl, type: RequestType, parser: (String) -> T): CompletableFuture<T>
    {
        var prototype = Request.Builder()
            .url(httpUrl)
            .addHeader("X-API-KEY", apiKey)

        prototype = when (type)
        {
            RequestType.GET -> prototype.get()
            RequestType.POST -> prototype.post(emptyRequestBody)
            RequestType.PUT -> prototype.put(emptyRequestBody)
            RequestType.PATCH -> prototype.patch(emptyRequestBody)
            RequestType.DELETE -> prototype.delete(emptyRequestBody)
        }

        val request = prototype.build()

        return CompletableFuture.supplyAsync({

            client.newCall(request).execute().use { response ->
                val strResponse = response.body?.string()

                if (!response.isSuccessful) {
                    val exception = GsonProvider.gson.fromJson(strResponse, SerialException::class.java)
                    throw RecoveryRequestException(httpUrl, exception)
                }

                if (strResponse.isNullOrEmpty()) {
                    throw RuntimeException("Response from $httpUrl is empty or null!")
                }

                parser(strResponse)
            }
        }, pool.executor).exceptionally {
            throw it
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

        return send(httpUrl, RequestType.POST) {
            GsonProvider.gson.fromJson(it, Player::class.java)
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

        return send(httpUrl, RequestType.GET) {
            GsonProvider.gson.fromJson(it, ValidCode::class.java)
        }
    }

    fun sendCheckPremium(username: String): CompletableFuture<Boolean>
    {
        val httpUrl = HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .port(port)
            .addPathSegments("player/premium/check")
            .addQueryParameter("username", username)
            .build()

        return send(httpUrl, RequestType.GET) {
            it.toBooleanStrict()
        }
    }

}
