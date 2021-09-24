package tech.thatgravyboat.rewardclaim.types

import gg.essential.universal.ChatColor
import java.awt.Color

@Suppress("unused")
enum class RewardRarity(val translationKey: String, val color: ChatColor) {
    COMMON("rarity.common", ChatColor.WHITE),
    RARE("rarity.rare", ChatColor.AQUA),
    EPIC("rarity.epic", ChatColor.DARK_PURPLE),
    LEGENDARY("rarity.legendary", ChatColor.GOLD);
}