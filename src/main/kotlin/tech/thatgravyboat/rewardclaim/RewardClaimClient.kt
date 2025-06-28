package tech.thatgravyboat.rewardclaim

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.ChatFormatting
import tech.thatgravyboat.rewardclaim.remote.RemoteData
import tech.thatgravyboat.rewardclaim.ui.RewardClaimScreens

object RewardClaimClient : ClientModInitializer {

    override fun onInitializeClient() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
            val remote = RemoteData.get()
            if (remote.disabled) return@register true

            val text = ChatFormatting.stripFormatting(message.string)?.replace("\n", "") ?: return@register true
            val result = remote.rewardRegex.matchEntire(text) ?: return@register true
            val id = result.groups["id"]?.value ?: return@register true

            RewardClaimScreens.open(id)
            true
        }
    }
}