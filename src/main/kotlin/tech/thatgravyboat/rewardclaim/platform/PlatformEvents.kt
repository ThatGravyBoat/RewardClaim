package tech.thatgravyboat.rewardclaim.platform

import gg.essential.api.EssentialAPI
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import tech.thatgravyboat.rewardclaim.Initializer

object PlatformEvents {

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onChatMessage(event: ClientChatReceivedEvent) {
        if (Initializer.onMessage(event.message.unformattedText)) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onScreen(event: GuiOpenEvent) {
        if (Initializer.onOpenGui(EssentialAPI.getGuiUtil().openedScreen(), event.gui)) {
            event.isCanceled = true
        }
    }
}