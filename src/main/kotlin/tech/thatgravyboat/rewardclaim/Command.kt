package tech.thatgravyboat.rewardclaim

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler

class Command : Command("rewardclaim") {

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(Config.gui())
    }
}
