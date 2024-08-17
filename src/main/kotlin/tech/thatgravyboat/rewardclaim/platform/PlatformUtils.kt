package tech.thatgravyboat.rewardclaim.platform

import gg.essential.universal.utils.MCScreen
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
import net.minecraftforge.client.ClientCommandHandler

typealias MCBookScreen = net.minecraft.client.gui.GuiScreenBook

object PlatformUtils {

    fun fetch(url: String): String? {
        return gg.essential.api.utils.WebUtil.fetchString(url)
    }

    fun openScreen(screen: MCScreen?) {
        gg.essential.api.EssentialAPI.getGuiUtil().openScreen(screen)
    }

    fun registerCommand(command: String, callback: () -> Unit) {
        ClientCommandHandler.instance.registerCommand(SimpleCommand(command, Runnable(callback)))
    }

    class SimpleCommand(private val name: String, private val default: Runnable): CommandBase() {

        override fun getCommandName() = name
        override fun getCommandUsage(sender: ICommandSender) = "/$name"

        override fun processCommand(sender: ICommandSender, args: Array<out String>) {
            default.run()
        }

        override fun canCommandSenderUseCommand(sender: ICommandSender) = true
        override fun addTabCompletionOptions(sender: ICommandSender, args: Array<out String>, pos: BlockPos) =  mutableListOf<String>()
    }
}