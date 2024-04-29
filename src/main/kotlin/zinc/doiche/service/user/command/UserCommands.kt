package zinc.doiche.service.user.command

import org.bukkit.command.Command
import zinc.doiche.lib.annotation.CommandFactory
import zinc.doiche.lib.annotation.CommandRegistry
import zinc.doiche.lib.brigadier.CommandBuilder

@CommandRegistry
class UserCommands {
    @CommandFactory
    fun profile(): Command = CommandBuilder.simple("profile") { context, player ->


            CommandBuilder.SINGLE_SUCCESS
        }

    @CommandFactory
    fun menu(): Command = CommandBuilder.simple("profile") { context, player ->


        CommandBuilder.SINGLE_SUCCESS
    }
}