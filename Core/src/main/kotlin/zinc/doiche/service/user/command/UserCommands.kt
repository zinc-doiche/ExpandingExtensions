package zinc.doiche.service.user.command

import com.mojang.brigadier.Command
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import zinc.doiche.lib.*

@CommandRegistry
class UserCommands {
//    @CommandFactory
//    fun profile() = CommandBuilder.simple("profile") { _, player ->
//        player.openInventory(Bukkit.createInventory(null, 54, Component.text("profile")))
//        CommandBuilder.SINGLE_SUCCESS
//    }
//
//    @CommandFactory
//    fun menu() = Commands.literal("menu") { _, player ->
//        player.openInventory(Bukkit.createInventory(null, 54, Component.text("menu")))
//        CommandBuilder.SINGLE_SUCCESS
//    }

    @CommandFactory
    fun test() = Commands.literal("test")
        .requiresOp()
        .executesPlayer { context, player ->
            player.sendMessage(Component.text("test"))
            Command.SINGLE_SUCCESS
        }.build()
}