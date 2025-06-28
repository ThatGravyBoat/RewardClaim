package tech.thatgravyboat.rewardclaim.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec
import net.hypixel.data.type.GameType

object DataCodec {

    private val REWARD_CODEC = RecordCodecBuilder.create { it.group(
        Codec.STRING.fieldOf("reward").forGetter(Reward::reward),
        EnumCodec.of(RewardRarity::class.java).fieldOf("rarity").forGetter(Reward::rarity),
        Codec.INT.optionalFieldOf("amount", 0).forGetter(Reward::amount),
        EnumCodec.of(GameType::class.java).optionalFieldOf("gameType").forGetter(CodecExtras.optionalFor(Reward::gameType)),
        Codec.STRING.optionalFieldOf("package").forGetter(CodecExtras.optionalFor(Reward::`package`)),
        Codec.STRING.optionalFieldOf("key").forGetter(CodecExtras.optionalFor(Reward::key))
    ).apply(it) { reward, rarity, amt, type, pkg, key ->
        Reward(reward, rarity, amt, type.orElse(null), pkg.orElse(null), key.orElse(null))
    }  }

    private val STREAK_CODEC = RecordCodecBuilder.create { it.group(
        Codec.INT.fieldOf("value").forGetter(Streak::value),
        Codec.INT.fieldOf("score").forGetter(Streak::score),
        Codec.INT.fieldOf("highScore").forGetter(Streak::highScore)
    ).apply(it, ::Streak) }

    private val AD_CODEC = RecordCodecBuilder.create { it.group(
        Codec.INT.fieldOf("duration").forGetter(Ad::duration),
        Codec.STRING.fieldOf("link").forGetter(Ad::link)
    ).apply(it, ::Ad) }

    val CODEC = RecordCodecBuilder.create { it.group(
        REWARD_CODEC.listOf().fieldOf("rewards").forGetter(Data::rewards),
        STREAK_CODEC.fieldOf("dailyStreak").forGetter(Data::dailyStreak),
        Codec.INT.fieldOf("activeAd").forGetter(Data::activeAd),
        AD_CODEC.optionalFieldOf("ad").forGetter(CodecExtras.optionalFor(Data::ad)),
        Codec.STRING.fieldOf("id").forGetter(Data::id),
        Codec.BOOL.optionalFieldOf("skippable", false).forGetter(Data::skippable)
    ).apply(it) { rewards, streak, activeAd, ad, id, skippable ->
        Data(rewards, streak, activeAd, ad.orElse(null), id, skippable)
    } }
}