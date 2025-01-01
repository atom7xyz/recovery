package xyz.atom7.recoveryVelocity.connectivity

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.spongepowered.configurate.CommentedConfigurationNode
import xyz.atom7.recoveryVelocity.exceptions.PremiumRequestException
import xyz.atom7.recoveryVelocity.providers.GsonProvider
import xyz.atom7.recoveryVelocity.serialization.SerialException
import xyz.sorridi.stone.common.threading.Pool
import java.util.concurrent.CompletableFuture

class PremiumRequest(config: CommentedConfigurationNode)
{
    private val pool: Pool = Pool(1)
    private val client: OkHttpClient = OkHttpClient()

    private val webNode = config.node("web")
    private val scheme  = webNode.node("scheme").getString("http")
    private val host    = webNode.node("host").getString("http")
    private val port    = webNode.node("port").getInt(8080)
    private val apiKey  = webNode.node("api-key").getString("changeme")

    private val domainsNode = config.node("domains")
    val premiumDomain: String = domainsNode.node("premium").getString("changeme")
    val crackedDomain: String = domainsNode.node("cracked").getString("changeme")

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

            client.newCall(request).execute().use { response ->
                val strResponse = response.body?.string()

                if (!response.isSuccessful) {
                    val exception = GsonProvider.gson.fromJson(strResponse, SerialException::class.java)
                    throw PremiumRequestException(httpUrl, exception)
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

    fun sendPremiumPlayer(username: String, premium: Boolean)
    {
        val httpUrl = HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .port(port)
            .addPathSegments("player/premium/set")
            .addQueryParameter("username", username)
            .addQueryParameter("premium", premium.toString())
            .build()

        send(httpUrl) { }
    }

    fun removePremiumPlayer(username: String)
    {
        val httpUrl = HttpUrl.Builder()
            .scheme(scheme)
            .host(host)
            .port(port)
            .addPathSegments("player/premium/remove")
            .addQueryParameter("username", username)
            .build()

        send(httpUrl) { }
    }

}
