package tech.thatgravyboat.rewardclaim.types

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.minecraft.util.JsonUtils
import tech.thatgravyboat.rewardclaim.RewardLanguage
import java.util.regex.Matcher

private val GSON = Gson()

class WebData(security : Matcher, data : Matcher, i18n : Matcher) {

    val securityToken : String = security.group("token")
    val language : RewardLanguage = RewardLanguage(i18n.group("translations"))

    val rewards = mutableListOf<RewardData>()
    var streak : StreakData = StreakData(0,0,0)
    var activeAd : Int = 0
    var adLink : String = "https://store.hypixel.net/?utm_source=rewards-video&utm_medium=website&utm_content=TRsCiBNYY7M&utm_campaign=Rewards"
    var skippable : Boolean = false
    var duration : Int = 30

    init {
        val json = GSON.fromJson(data.group("data"), JsonObject::class.java)
        json.get("rewards")?.let {
            if (it.isJsonArray) {
                it.asJsonArray.forEach { jsonObject ->
                    val rewardObject = jsonObject.asJsonObject
                    rewards.add(RewardData(
                            RewardRarity.valueOf(rewardObject.get("rarity").asString!!),
                            rewardObject.get("reward").asString!!,
                            if (rewardObject.has("amount")) rewardObject.get("amount").asInt else null,
                            if (rewardObject.has("intlist")) rewardObject.get("intlist").asJsonArray.size() else null,
                            if (rewardObject.has("gameType")) GameMode.getModeFromId(rewardObject.get("gameType").asString) else null,
                            JsonUtils.getString(rewardObject, "package", null),
                            JsonUtils.getString(rewardObject, "key", null),
                    ))
                }
            }
        }

        json.get("dailyStreak")?.let {
            val streakObject = it.asJsonObject
            streak = StreakData(JsonUtils.getInt(streakObject, "value", 0),
                    JsonUtils.getInt(streakObject, "score", 0),
                    JsonUtils.getInt(streakObject, "highScore", 0)
            )
        }

        json.get("activeAd")?.let { activeAd = it.asInt }
        json.get("ad")?.let { jsonObject ->
            val adObject = jsonObject.asJsonObject
            adObject.get("link")?.let { adLink = it.asString }
            adObject.get("duration")?.let { duration = it.asInt }
        }
        json.get("skippable")?.let { skippable = it.asBoolean }

    }

}