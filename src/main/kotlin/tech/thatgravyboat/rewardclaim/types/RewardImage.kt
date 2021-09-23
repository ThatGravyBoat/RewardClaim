package tech.thatgravyboat.rewardclaim.types

import java.net.MalformedURLException
import java.net.URL

class RewardImage(private val urlString: String, val height: Int) {
    val url: URL?
        get() {
            return try {
                URL(urlString)
            } catch (ignored: MalformedURLException) {
                null
            }
        }
}