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

@Mod(
    name = "RewardClaim",
    modid = "gravyrewardclaim",
    version = "1.0.4",
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object RewardClaim {

    private var rewardClaimTime: Long = 0
    var debugMode = false

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent?) {
        MinecraftForge.EVENT_BUS.register(this)
        Command().register()
    }

    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent?) {
        ExternalConfiguration.loadData()
    }

    @SubscribeEvent
    fun onChatMessage(event: ClientChatReceivedEvent) {
        ExternalConfiguration.rewardMessageRegex.matchEntire(event.message.unformattedText.trim())
            ?.apply {
                if (!ExternalConfiguration.disabled) {
                    EssentialAPI.getGuiUtil().openScreen(RewardClaimGui(groups["id"]!!.value))
                    rewardClaimTime = System.currentTimeMillis()
                } else {
                    EssentialAPI.getNotifications()
                        .push("Mod Disabled", ExternalConfiguration.disabledMessage)
                }
            }

        ExternalConfiguration.rewardMissedMessageRegex.matchEntire(event.message.unformattedText.trim())
            ?.apply {
                EssentialAPI.getNotifications().push(
                    "Reward Claim Missed!",
                    "You missed a reward claim, click on this to open the reward claim gui to claim your reward."
                ) {
                    if (!ExternalConfiguration.disabled) {
                        EssentialAPI.getGuiUtil().openScreen(RewardClaimGui(groups["id"]!!.value))
                    } else {
                        EssentialAPI.getNotifications()
                            .push("Mod Disabled", ExternalConfiguration.disabledMessage)
                    }
                }
                event.isCanceled = true
            }
    }

    @SubscribeEvent
    fun onScreen(event: GuiOpenEvent) {
        if (debugMode) {
            println("-------------------------------------------------------------------------------")
            println("[Reward Claim Debug] : Current Screen = ${EssentialAPI.getGuiUtil().openedScreen()?.javaClass?.name ?: "null"}")
            println("[Reward Claim Debug] : Screen = ${event.gui?.javaClass?.name ?: "null"}")
            println("[Reward Claim Debug] : Reward Time = $rewardClaimTime")
            println("-------------------------------------------------------------------------------")
        }
        if (EssentialAPI.getGuiUtil().openedScreen() is RewardClaimGui &&
            (event.gui is GuiScreenBook || event.gui == null) && System.currentTimeMillis() - rewardClaimTime <= Config.checkingTimer
        ) {
            event.isCanceled = true
        }
    }
}
