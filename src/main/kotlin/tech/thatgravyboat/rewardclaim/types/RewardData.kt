package tech.thatgravyboat.rewardclaim.types

import com.google.gson.annotations.SerializedName
import tech.thatgravyboat.rewardclaim.RewardConfiguration
import tech.thatgravyboat.rewardclaim.RewardLanguage
import java.util.*


private val ARMOR_PIECE_REGEX = Regex("^[a-z0-9_]+_([a-z]+)$", RegexOption.IGNORE_CASE)
private val ARMOR_REGEX = Regex("_([a-z]+)$", RegexOption.IGNORE_CASE)

data class RewardData(
    val rarity: RewardRarity,
    private val reward: String,
    val amount: Int?,
    val intlist: Array<Int>?,
    private val gameType: GameMode?,
    @SerializedName("package") private val rewardPackage: String?,
    @SerializedName("key") private val rewardKey: String?
) {

    fun getDisplayName(language: RewardLanguage): String {
        rewardPackage?.let { item ->
            if (reward.equals("housing_package", ignoreCase = true)) {
                return "${rarity.color}${
                    language.translate(
                        "housing.skull." + item.replace(
                            "specialoccasion_reward_card_skull_",
                            ""
                        )
                    )
                }"
            }
        }
        rewardKey?.let { key ->
            if (reward.equals("add_vanity", ignoreCase = true)) {
                val pieceMatcher = ARMOR_PIECE_REGEX.find(key)
                val armorMatcher = ARMOR_REGEX.find(key)
                if ("suit" in key && pieceMatcher != null && armorMatcher != null) {
                    return "${rarity.color}${language.translate("vanity." + armorMatcher.groups[1]!!.value)} ${
                        language.translate(
                            "vanity.armor." + pieceMatcher.groups[1]!!.value
                        )
                    }"
                } else if ("emote" in key || "taunt" in key) {
                    return "${rarity.color}${language.translate("vanity.$key")}"
                }
            }
        }
        return if (reward.equals("tokens", ignoreCase = true) || reward.equals("coins", ignoreCase = true)) {
            "${rarity.color}${language.translate("type.$reward").replace("{\$game}", gameType!!.displayName)}"
        } else {
            "${rarity.color}${language.translate("type.$reward")}"
        }
    }

    fun getDescription(language: RewardLanguage): String {
        rewardKey?.let { key ->
            if (reward.equals("add_vanity", ignoreCase = true)) {
                when {
                    "suit" in key -> return language.translate("vanity.suits.description")
                    "emote" in key -> return language.translate("vanity.emotes.description")
                    "taunt" in key -> return language.translate("vanity.gestures.description")
                }
            }
        }
        return if (reward.equals("tokens", ignoreCase = true) || reward.equals("coins", ignoreCase = true)) {
            "${rarity.color}${
                language.translate("type.$reward.description").replace("{\$game}", gameType!!.displayName)
            }"
        } else {
            "${rarity.color}${language.translate("type.$reward.description")}"
        }
    }

    val image: RewardImage?
        get() {
            var id = reward.lowercase(Locale.ROOT)
            rewardKey?.let { key ->
                if (id == "add_vanity") {
                    when {
                        "suit" in key -> id += "_suit"
                        "emote" in key -> id += "_emote"
                        "taunt" in key -> id += "_taunt"
                    }
                }
            }
            rewardPackage?.let { item ->
                if (id == "housing_package") {
                    val packageId = item.replace("specialoccasion_reward_card_skull_", "")
                    RewardConfiguration.textures[id + "_" + packageId]?.let { image -> return image }
                }
            }
            return RewardConfiguration.textures[id]
        }
}