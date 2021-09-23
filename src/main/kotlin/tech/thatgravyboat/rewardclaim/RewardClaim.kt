package tech.thatgravyboat.rewardclaim

import gg.essential.api.EssentialAPI
import net.minecraft.client.gui.GuiScreenBook
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import tech.thatgravyboat.rewardclaim.ui.RewardClaimGui

@Mod(name = "RewardClaim", modid = "gravyrewardclaim", version = "1.0.0", modLanguageAdapter = "tech.thatgravyboat.rewardclaim.adapter.KotlinLanguageAdapter")
object ForgeTemplate {

    private var rewardClaimTime: Long = 0

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent?) {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent?) {
        RewardConfiguration.loadData()
    }

    @SubscribeEvent
    fun onChatMessage(event: ClientChatReceivedEvent) {
        val rewardMatcher = RewardConfiguration.rewardMessageRegex.matcher(event.message.unformattedText.trim())
        val rewardMissedMatcher = RewardConfiguration.rewardMissedMessageRegex.matcher(event.message.unformattedText.trim())

        if (rewardMatcher.matches()) {
            EssentialAPI.getGuiUtil().openScreen(RewardClaimGui(rewardMatcher.group("id")))
            rewardClaimTime = System.currentTimeMillis()
        }
        if (rewardMissedMatcher.matches()) {
            EssentialAPI.getNotifications().push(
                    "Reward Claim Missed!",
                    "You missed a reward claim, click on this to open the reward claim gui to claim your reward.",
                    { EssentialAPI.getGuiUtil().openScreen(RewardClaimGui(rewardMissedMatcher.group("id"))) }
            )
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onScreen(event: GuiOpenEvent) {
        if (EssentialAPI.getGuiUtil().openedScreen() is RewardClaimGui && event.gui is GuiScreenBook && System.currentTimeMillis() - rewardClaimTime <= 3000){
            event.isCanceled = true
            rewardClaimTime = 0
        }
    }

}