package zinc.doiche.service.user.command

import com.mojang.brigadier.Command
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import zinc.doiche.lib.*

@CommandRegistry
class UserCommands {
    @CommandFactory
    fun profile() = simpleCommand("profile") { context ->
        context.source.player?.openInventory(Bukkit.createInventory(null, 54, Component.text("profile")))
        Command.SINGLE_SUCCESS
    }

    @CommandFactory
    fun menu() = simpleCommand("menu") { context ->
        context.source.player?.openInventory(Bukkit.createInventory(null, 54, Component.text("menu")))
        Command.SINGLE_SUCCESS
    }

    @CommandFactory
    fun test() = Commands.literal("test")
        .requiresOp()
        .executesPlayer { context, player ->
            player.sendMessage(Component.text("test"))
            Command.SINGLE_SUCCESS
        }.build()
}