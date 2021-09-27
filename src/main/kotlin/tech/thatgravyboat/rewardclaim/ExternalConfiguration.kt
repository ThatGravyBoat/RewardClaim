package tech.thatgravyboat.rewardclaim

import com.google.gson.Gson
import gg.essential.api.utils.WebUtil
import tech.thatgravyboat.rewardclaim.types.ImageType
import tech.thatgravyboat.rewardclaim.types.RewardImage

private val GSON = Gson()
private val DEFAULT_IMAGE_TYPE = ImageType(142, 100, false)

object ExternalConfiguration {
    private lateinit var imageTypes: HashMap<String, ImageType>
    lateinit var textures: HashMap<String, RewardImage>
    lateinit var rewardMessageRegex: Regex
    lateinit var rewardMissedMessageRegex: Regex
    lateinit var userAgent: String
    var disabled = false
    lateinit var disabledMessage: String

    fun getImageType(type: String?) = if (type == null) DEFAULT_IMAGE_TYPE else imageTypes.getOrDefault(type, DEFAULT_IMAGE_TYPE)

    fun loadData() {
        WebUtil.fetchString("https://raw.githubusercontent.com/ThatGravyBoat/RewardClaim/master/data.json")?.let {
            val config = GSON.fromJson(it, JsonConfig::class.java)
            textures = config.textures
            imageTypes = config.imageTypes
            rewardMessageRegex = Regex(config.rewardRegex)
            rewardMissedMessageRegex = Regex(config.missedRewardRegex)
            userAgent = config.userAgent
            disabled = config.disabled
            disabledMessage = config.disabledMessage
        }
    }

    private data class JsonConfig(
        val imageTypes: HashMap<String, ImageType> = hashMapOf(),
        val textures: HashMap<String, RewardImage> = hashMapOf(),
        val rewardRegex: String = "Click the link to visit our website and claim your reward: https://rewards\\.hypixel\\.net/claim-reward/(?<id>[A-Za-z0-9]{8})",
        val missedRewardRegex: String = "We noticed you haven't claimed your free Daily Reward yet!\\nTo choose your reward you have to click the link to visit our website! As a reminder, here's your link for today:  https://rewards\\.hypixel\\.net/claim-reward/(?<id>[A-Za-z0-9]{8})",
        val userAgent: String = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36",
        val disabled: Boolean = false,
        val disabledMessage: String = "Reward Claim was disabled by the mod author for an unknown reason."
    )
}