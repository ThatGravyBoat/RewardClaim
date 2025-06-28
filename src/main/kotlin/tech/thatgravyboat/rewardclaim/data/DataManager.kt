package tech.thatgravyboat.rewardclaim.data

import tech.thatgravyboat.rewardclaim.remote.RemoteData
import java.net.CookieManager
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

object DataManager {

    private const val CLAIM_URL = "https://rewards.hypixel.net/claim-reward/claim"
    private const val URL = "https://rewards.hypixel.net/claim-reward/"

    private val SECURITY_REGEX = Regex("window\\.securityToken = \"(?<token>.*)\";")
    private val DATA_REGEX = Regex("window\\.appData = '(?<data>\\{.*})';")
    private val I18N_REGEX = Regex("window.i18n = \\{(?<translations>.*)};", RegexOption.DOT_MATCHES_ALL)

    private val cookies = CookieManager()
    private val http = HttpClient.newBuilder()
        .cookieHandler(cookies)
        .build()

    fun get(id: String): CompletableFuture<RewardState> = CompletableFuture.supplyAsync {
        val request = HttpRequest.newBuilder(URI.create("$URL$id"))
            .GET()
            .version(HttpClient.Version.HTTP_2)
            .header("User-Agent", RemoteData.get().userAgent)
            .build()

        val output = http.send(request, HttpResponse.BodyHandlers.ofString()).body()

        val security = SECURITY_REGEX.find(output) ?: error("Security token not found in response for ID: $id")
        val data = DATA_REGEX.find(output) ?: error("Data not found in response for ID: $id")
        val i18n = I18N_REGEX.find(output) ?: error("Translations not found in response for ID: $id")

        RewardState.get(security, data, i18n)
    }

    fun claim(state: RewardState, reward: Reward): CompletableFuture<Void> = CompletableFuture.runAsync {
        val data = state.data
        val selected = data.rewards.indexOfFirst { it == reward }
        val url = "$CLAIM_URL?option=$selected&id=${data.id}&activeAd=${data.activeAd}&_csrf=${state.token}&watchedFallback=false"

        val request = HttpRequest.newBuilder(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())
            .version(HttpClient.Version.HTTP_2)
            .header("User-Agent", RemoteData.get().userAgent)
            .build()

        val res = http.send(request, HttpResponse.BodyHandlers.ofString())

        if (res.statusCode() != 200) {
            error(
                "Failed to claim reward: $reward for " +
                "ID: ${data.id} with status code: ${res.statusCode()}, " +
                "response: ${res.body()}"
            )
        }
    }
}