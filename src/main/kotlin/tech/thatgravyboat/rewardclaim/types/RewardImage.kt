package tech.thatgravyboat.rewardclaim.types

import com.google.gson.annotations.SerializedName
import java.net.MalformedURLException
import java.net.URL

data class RewardImage(@SerializedName("url") private val urlIn: String, val imageType: String) {
    val url: URL?
        get() {
            return try {
                URL(urlIn)
            } catch (ignored: MalformedURLException) {
                null
            }
        }
}