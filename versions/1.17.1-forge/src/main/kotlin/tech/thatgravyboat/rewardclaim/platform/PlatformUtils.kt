package tech.thatgravyboat.rewardclaim.platform

import gg.essential.universal.utils.MCScreen
import net.minecraft.client.gui.screens.inventory.BookViewScreen

typealias MCBookScreen = BookViewScreen

object PlatformUtils {

    fun fetch(url: String): String? {
        return null
    }

    fun openScreen(screen: MCScreen?) {
    }

    fun registerCommand(command: String, callback: () -> Unit) {
    }
}