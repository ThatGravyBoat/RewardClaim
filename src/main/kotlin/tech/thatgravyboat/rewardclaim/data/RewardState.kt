package tech.thatgravyboat.rewardclaim.data

import com.mojang.serialization.JsonOps
import net.minecraft.util.GsonHelper

data class RewardState(
    val token: String,
    val data: Data,
    val language: Language,
) {

    companion object {

        fun get(security: MatchResult, data: MatchResult, i18n: MatchResult): RewardState {
            val token = security.groups["token"]?.value ?: error("Security token not found")
            val jsonData = data.groups["data"]?.value ?: error("Data not found")
            val translations = i18n.groups["translations"]?.value ?: error("Translations not found")

            return RewardState(
                token,
                DataCodec.CODEC.parse(JsonOps.INSTANCE, GsonHelper.parse(jsonData)).orThrow,
                Language(translations)
            )
        }
    }
}
