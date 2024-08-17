package tech.thatgravyboat.rewardclaim.platform

import gg.essential.universal.utils.MCScreen
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.BookScreen
import tech.thatgravyboat.rewardclaim.ui.RewardClaimGui
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

typealias MCBookScreen = BookScreen

object PlatformUtils {

    private val client = HttpClient.newBuilder().build()

    fun fetch(url: String): String? {
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(url))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) return null
        return response.body()
    }

    fun openScreen(screen: MCScreen?) {
        MinecraftClient.getInstance().send {
            MinecraftClient.getInstance().setScreen(screen)
        }
    }

    fun registerCommand(command: String, callback: () -> Unit) {
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher, _ ->
            dispatcher.register(ClientCommandManager.literal(command).executes { callback(); 1 })
        })
    }
}