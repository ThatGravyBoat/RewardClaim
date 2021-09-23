package tech.thatgravyboat.rewardclaim

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraft.util.JsonUtils
import tech.thatgravyboat.rewardclaim.types.RewardImage
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.regex.Pattern

private val GSON = Gson()

object RewardConfiguration {
    val TEXTURES: MutableMap<String, RewardImage> = HashMap<String, RewardImage>()
    var rewardMessageRegex: Pattern = Pattern.compile("Click the link to visit our website and claim your reward: https://rewards\\.hypixel\\.net/claim-reward/(?<id>[A-Za-z0-9]{8})")
    var rewardMissedMessageRegex: Pattern = Pattern.compile("We noticed you haven't claimed your free Daily Reward yet!\\nTo choose your reward you have to click the link to visit our website! As a reminder, here's your link for today:  https://rewards\\.hypixel\\.net/claim-reward/(?<id>[A-Za-z0-9]{8})")
    var userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36"

    fun loadData() {
        val json = GSON.fromJson(readData(), JsonObject::class.java)

        json.get("textures")?.apply {
            for (texture in asJsonArray) {
                val image = texture.asJsonObject
                TEXTURES[image["id"].asString] = RewardImage(image["url"].asString, JsonUtils.getInt(image, "height", 142))
            }
        }

        json.get("rewardRegex")?.apply { rewardMessageRegex = Pattern.compile(asString) }
        json.get("missedRewardRegex")?.apply { rewardMissedMessageRegex = Pattern.compile(asString) }
        json.get("userAgent")?.apply { userAgent = asString }
    }

    private fun readData(): String {
        try {
            Scanner(
                URL("https://gist.githubusercontent.com/ThatGravyBoat/05cf118ea1daced936f040a41a648819/raw/2410c868444b073fd212fbed1da5a063d79dc816/data.json").openStream(),
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
}