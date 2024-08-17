package tech.thatgravyboat.rewardclaim.platform

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import tech.thatgravyboat.rewardclaim.Initializer

object PlatformEvents {

    init {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
            !Initializer.onMessage(message = message.string)
        }
    }
}