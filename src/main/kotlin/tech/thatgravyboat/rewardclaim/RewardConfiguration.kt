package tech.thatgravyboat.rewardclaim

import com.google.gson.Gson
import tech.thatgravyboat.rewardclaim.types.ImageType
import tech.thatgravyboat.rewardclaim.types.RewardImage
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

private val GSON = Gson()
private val DEFAULT_IMAGE_TYPE = ImageType(142, 100, false)

object RewardConfiguration {
    private lateinit var imageTypes: HashMap<String, ImageType>
    lateinit var textures: HashMap<String, RewardImage>
    lateinit var rewardMessageRegex: Regex
    lateinit var rewardMissedMessageRegex: Regex
    lateinit var userAgent: String

    fun getImageType(type: String?) = if (type == null) DEFAULT_IMAGE_TYPE else imageTypes.getOrDefault(type, DEFAULT_IMAGE_TYPE)

    fun loadData() {
        val config = GSON.fromJson(readData(), JsonConfig::class.java)
        textures = config.textures
        imageTypes = config.imageTypes
        rewardMessageRegex = Regex(config.rewardRegex)
        rewardMissedMessageRegex = Regex(config.missedRewardRegex)
        userAgent = config.userAgent
    }

    private fun readData(): String {
        try {
            Scanner(
                URL("https://raw.githubusercontent.com/ThatGravyBoat/RewardClaim/master/data.json").openStream(),
                "UTF-8"
            ).use { scanner ->
                scanner.useDelimiter("\\A")
                return if (scanner.hasNext()) scanner.next() else ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    private data class JsonConfig(
        val imageTypes: HashMap<String, ImageType> = hashMapOf(),
        val textures: HashMap<String, RewardImage> = hashMapOf(),
        val rewardRegex: String = "Click the link to visit our website and claim your reward: https://rewards\\.hypixel\\.net/claim-reward/(?<id>[A-Za-z0-9]{8})",
        val missedRewardRegex: String = "We noticed you haven't claimed your free Daily Reward yet!\\nTo choose your reward you have to click the link to visit our website! As a reminder, here's your link for today:  https://rewards\\.hypixel\\.net/claim-reward/(?<id>[A-Za-z0-9]{8})",
        val userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36"
    )
}