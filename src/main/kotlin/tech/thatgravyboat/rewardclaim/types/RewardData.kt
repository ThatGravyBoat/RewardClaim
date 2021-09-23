package tech.thatgravyboat.rewardclaim.types

import tech.thatgravyboat.rewardclaim.RewardConfiguration
import tech.thatgravyboat.rewardclaim.RewardLanguage
import java.util.*
import java.util.regex.Pattern


private val ARMOR_PIECE_REGEX = Pattern.compile("^[a-z0-9_]+_([a-z]+)$", Pattern.CASE_INSENSITIVE)
private val ARMOR_REGEX = Pattern.compile("_([a-z]+)$", Pattern.CASE_INSENSITIVE)

class RewardData(val rarity: RewardRarity,
                 private val reward : String,
                 val amount : Int?,
                 val boxes : Int?,
                 private val gameMode : GameMode?,
                 private val rewardPackage : String?,
                 private val rewardKey : String?
                 ) {

    fun getDisplayName(language: RewardLanguage): String {
        rewardPackage?.let { item ->
            if (reward.equals("housing_package", ignoreCase = true)) {
                return "${rarity.color}${language.translate("housing.skull." + item.replace("specialoccasion_reward_card_skull_", ""))}"
            }
        }
        rewardKey?.let { key ->
            if (reward.equals("add_vanity", ignoreCase = true)) {
                val pieceMatcher = ARMOR_PIECE_REGEX.matcher(key)
                val armorMatcher = ARMOR_REGEX.matcher(key)
                if ("suit" in key && pieceMatcher.find() && armorMatcher.find()) {
                    return "${rarity.color}${language.translate("vanity." + armorMatcher.group(1))} ${language.translate("vanity.armor." + pieceMatcher.group(1))}"
                } else if ("emote" in key || "taunt" in key) {
                    return "${rarity.color}${language.translate("vanity.$key")}"
                }
            }
        }
        return if (reward.equals("tokens", ignoreCase = true) || reward.equals("coins", ignoreCase = true)) {
            "${rarity.color}${language.translate("type.$reward").replace("{\$game}", gameMode!!.displayName)}"
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
            "${rarity.color}${language.translate("type.$reward.description").replace("{\$game}", gameMode!!.displayName)}"
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
                    RewardConfiguration.TEXTURES[id + "_" + packageId]?.let { image -> return image }
                }
            }
            return RewardConfiguration.TEXTURES[id]
        }
}