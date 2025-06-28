package tech.thatgravyboat.rewardclaim.data

import net.hypixel.data.type.GameType
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import tech.thatgravyboat.rewardclaim.remote.RemoteData
import tech.thatgravyboat.rewardclaim.remote.RemoteImage

private val ARMOR_REGEX = Regex("(^[a-z0-9_]+)_([a-z]+)$", RegexOption.IGNORE_CASE)

data class Data(
    val rewards: List<Reward>,
    val dailyStreak: Streak,
    val activeAd: Int,
    val ad: Ad?,
    val id: String,
    val skippable: Boolean,
)

data class Ad(val duration: Int, val link: String)
data class Streak(val value: Int, val score: Int, val highScore: Int)
data class Reward(
    val reward: String,
    val rarity: RewardRarity,
    val amount: Int,
    val gameType: GameType?,
    val `package`: String?,
    val key: String?,
) {

    val image: RemoteImage? get() {
        var id = reward.lowercase()
        if (id == "add_vanity" && key != null) {
            when {
                "suit" in key -> id += "_suit"
                "emote" in key -> id += "_emote"
                "taunt" in key -> id += "_taunt"
            }
        }
        if (`package` != null && reward.equals("housing_package", true)) {
            val newId = `package`.replace("specialoccasion_reward_card_skull_", "")
            val image = RemoteData.get().textures["${id}_${newId}"]
            if (image != null) {
                return image
            }
        }

        return RemoteData.get().textures[id]
    }

    fun getDisplayName(language: Language): Component {
        if (`package` != null && reward.equals("housing_package", true)) {
            val key = "housing.skull.${`package`.replace("specialoccasion_reward_card_skull_", "")}"
            return Component.literal(language[key]).withStyle(rarity.color)
        }

        if (key != null && reward.equals("add_vanity", true)) {
            val match = ARMOR_REGEX.find(key)
            if ("suit" in key && match != null) {
                val prefix = "vanity.${match.groupValues[1]}"
                val suffix = "vanity.armor.${match.groupValues[2]}"
                return Component.literal("${language[prefix]} ${language[suffix]}").withStyle(rarity.color)
            } else if ("emote" in key || "taunt" in key) {
                return Component.literal(language["vanity.${key}"]).withStyle(rarity.color)
            }
        }

        if (reward.equals("tokens", true) || reward.equals("coins", true)) {
            return Component.literal(language["type.$reward"].replace("{\$game}", gameType?.getName() ?: "Unknown")).withStyle(rarity.color)
        }

        return Component.literal(language["type.$reward"]).withStyle(rarity.color)
    }

    fun getDisplayDescription(language: Language): Component {
        if (key != null && reward.equals("add_vanity", true)) {
            when {
                "suit" in key -> Component.literal(language["vanity.suits.description"])
                "emote" in key -> Component.literal(language["vanity.emotes.description"])
                "taunt" in key -> Component.literal(language["vanity.gestures.description"])
            }
        }

        val description = language["type.${reward}.description"]

        if (reward.equals("tokens", true) || reward.equals("coins", true)) {
            return Component.literal(description.replace("{\$game}", gameType?.getName() ?: "Unknown")).withStyle(rarity.color)
        }

        return Component.literal(description).withStyle(rarity.color)
    }
}

enum class RewardRarity(val key: String, val color: ChatFormatting) {
    COMMON("rarity.common", ChatFormatting.WHITE),
    RARE("rarity.rare", ChatFormatting.AQUA),
    EPIC("rarity.epic", ChatFormatting.DARK_PURPLE),
    LEGENDARY("rarity.legendary", ChatFormatting.GOLD);

    fun getDisplayName(language: Language): Component {
        return Component.literal(language[key]).withStyle(color)
    }
}