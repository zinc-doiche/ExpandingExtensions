package zinc.doiche.service.user.command

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import zinc.doiche.lib.annotation.CommandFactory
import zinc.doiche.lib.annotation.CommandRegistry
import zinc.doiche.lib.brigadier.CommandBuilder

@CommandRegistry
class UserCommands {
    @CommandFactory
    fun profile(): Command = CommandBuilder.simple("profile") { _, player ->
        player.openInventory(Bukkit.createInventory(null, 54, Component.text("profile")))
        CommandBuilder.SINGLE_SUCCESS
    }

    @CommandFactory
    fun menu(): Command = CommandBuilder.simple("menu") { _, player ->
        player.openInventory(Bukkit.createInventory(null, 54, Component.text("menu")))
        CommandBuilder.SINGLE_SUCCESS
    }
}