package tech.thatgravyboat.rewardclaim.remote

import com.mojang.serialization.JsonOps
import com.teamresourceful.resourcefullib.common.utils.WebUtils
import net.minecraft.util.GsonHelper
import java.net.URI
import java.util.concurrent.CompletableFuture

val DEFAULT_IMAGE_TYPE = RemoteImageType(142, 100, false)

data class RemoteImageType(val height: Int, val width: Int, val center: Boolean)
data class RemoteImage(val url: URI, val imageType: String)

data class RemoteData(
    val imageTypes: Map<String, RemoteImageType> = mapOf(),
    val textures: Map<String, RemoteImage> = mapOf(),
    val rewardRegex: Regex = Regex("Click the link to visit our website and claim your reward: https://rewards\\.hypixel\\.net/claim-reward/(?<id>[A-Za-z0-9]{8})"),
    val missedRewardRegex: Regex = Regex("We noticed you haven't claimed your free Daily Reward yet!\\nTo choose your reward you have to click the link to visit our website! As a reminder, here's your link for today:  https://rewards\\.hypixel\\.net/claim-reward/(?<id>[A-Za-z0-9]{8})"),
    val userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36",
    val disabled: Boolean = false,
) {

    companion object {

        private val DEFAULT_REMOTE_DATA = RemoteData()
        private var instance: RemoteData? = null

        init {
            CompletableFuture.runAsync {
                val data = WebUtils.get("https://raw.githubusercontent.com/ThatGravyBoat/RewardClaim/master/data.json") ?: error("Failed to fetch remote data.")
                val json = GsonHelper.parse(data)
                instance = RemoteCodec.CODEC.parse(JsonOps.INSTANCE, json).orThrow
            }
        }

        fun get(): RemoteData {
            return instance ?: DEFAULT_REMOTE_DATA
        }
    }
}