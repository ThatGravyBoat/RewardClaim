package tech.thatgravyboat.rewardclaim.types

import com.google.gson.annotations.SerializedName

data class StreakData(
    @SerializedName("value") val progress: Int,
    @SerializedName("score") val current: Int,
    @SerializedName("highScore") val highest: Int
)
