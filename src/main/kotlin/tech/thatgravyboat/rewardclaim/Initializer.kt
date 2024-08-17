package tech.thatgravyboat.rewardclaim

import gg.essential.universal.utils.MCScreen
import tech.thatgravyboat.rewardclaim.platform.MCBookScreen
import tech.thatgravyboat.rewardclaim.platform.PlatformEvents
import tech.thatgravyboat.rewardclaim.platform.PlatformUtils
import tech.thatgravyboat.rewardclaim.ui.RewardClaimGui

object Initializer {

    private var rewardClaimTime: Long = 0

    fun init() {
        PlatformEvents
        ExternalConfiguration.loadData()
        PlatformUtils.registerCommand("rewardclaim") {
            PlatformUtils.openScreen(Config.gui())
        }
    }

    fun onMessage(message: String): Boolean {
        ExternalConfiguration.rewardMessageRegex.matchEntire(message.trim())
            ?.apply {
                if (!ExternalConfiguration.disabled) {
                    PlatformUtils.openScreen(RewardClaimGui(groups["id"]!!.value))
                    rewardClaimTime = System.currentTimeMillis()
                }
            }

        //#if MODERN==0
        ExternalConfiguration.rewardMissedMessageRegex.matchEntire(message.trim())
            ?.apply {
                gg.essential.api.EssentialAPI.getNotifications().push(
                    "Reward Claim Missed!",
                    "You missed a reward claim, click on this to open the reward claim gui to claim your reward."
                ) {
                    if (!ExternalConfiguration.disabled) {
                        PlatformUtils.openScreen(RewardClaimGui(groups["id"]!!.value))
                    }
                }
                return true
            }
        //#endif
        return false
    }

    fun onOpenGui(current: MCScreen?, gui: MCScreen?): Boolean {
        return current is RewardClaimGui &&
                (gui is MCBookScreen || gui == null) &&
                System.currentTimeMillis() - rewardClaimTime <= Config.checkingTimer
    }
}