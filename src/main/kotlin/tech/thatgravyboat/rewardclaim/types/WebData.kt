package tech.thatgravyboat.rewardclaim.types

import com.google.gson.Gson
import tech.thatgravyboat.rewardclaim.RewardLanguage

private val GSON = Gson()

class WebData(security: MatchResult, data: MatchResult, i18n: MatchResult) {

    val securityToken: String = security.groups["token"]!!.value
    val language: RewardLanguage = RewardLanguage(i18n.groups["translations"]!!.value)

    val rewards = mutableListOf<RewardData>()
    var streak: StreakData = StreakData(0, 0, 0)
    var activeAd: Int = 0
    var adLink: String =
        "https://store.hypixel.net/?utm_source=rewards-video&utm_medium=website&utm_content=TRsCiBNYY7M&utm_campaign=Rewards"
    var skippable: Boolean = false
    var duration: Int = 30

    init {
        GSON.fromJson(data.groups["data"]!!.value, JsonWebData::class.java)?.let {
            rewards.addAll(it.rewards)
            streak = it.dailyStreak
            activeAd = it.activeAd
            adLink = it.ad.link
            duration = it.ad.duration
            skippable = it.skippable
        }
    }

    private data class JsonWebData(
        val rewards: List<RewardData>,
        val dailyStreak: StreakData,
        val activeAd: Int,
        val ad: AdData,
        val skippable: Boolean
    )

}