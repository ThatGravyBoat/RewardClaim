package tech.thatgravyboat.rewardclaim

import gg.essential.elementa.components.image.ImageCache
import java.awt.image.BufferedImage
import java.net.URL

object MappedImageCache : ImageCache {

    private val IMAGES = hashMapOf<URL, BufferedImage>()

    override fun get(url: URL) = IMAGES[url]

    override fun set(url: URL, image: BufferedImage) {
        IMAGES[url] = image
    }
}