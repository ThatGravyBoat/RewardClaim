package tech.thatgravyboat.rewardclaim

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand

class Command : Command("rewardclaim") {

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(Config.gui())
    }

    @SubCommand("debug", description = "Toggles debug mode, do not turn this unless told otherwise by a dev.")
    fun debugMode() {
        RewardClaim.debugMode = !RewardClaim.debugMode
    }
}
