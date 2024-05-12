package zinc.doiche.lib.brigadier

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.command.VanillaCommandWrapper
import org.bukkit.entity.Player
import zinc.doiche.ExpandingExtensions.Companion.plugin
import zinc.doiche.lib.brigadier.argument.LiteralArgument
import zinc.doiche.lib.brigadier.argument.PlayerArgument
import zinc.doiche.lib.brigadier.argument.UUIDArgument
import zinc.doiche.lib.brigadier.argument.primitive.FloatArgument
import zinc.doiche.lib.brigadier.argument.primitive.IntArgument
import zinc.doiche.lib.brigadier.argument.primitive.StringArgument

/**
 * [com.mojang.brigadier]의 Wrapper.
 * @see CommandBuilder.literal
 */
interface CommandBuilder {
    fun build(): Command

    companion object {
        fun simple(
            name: String,
            executes: (CommandContext<CommandSourceStack>, Player) -> Int
        ) = literal(name)
            .requiresPlayer()
            .executesAsPlayer(executes)
            .build()

        fun command(builder: LiteralArgumentBuilder<CommandSourceStack>): Command {
            val craftServer: CraftServer = Bukkit.getServer() as CraftServer
            val minecraftServer: MinecraftServer = craftServer.server
            val commands: Commands = minecraftServer.commands
            val dispatcher: CommandDispatcher<CommandSourceStack> = commands.dispatcher

//            plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS.newHandler { event ->
//                event.registrar().register(
//                    plugin.pluginMeta,
//                    io.papermc.paper.command.brigadier.Commands.literal("")
//                        .build(),
//                    null,
//                    emptyList()
//                )
//            })

            return VanillaCommandWrapper(commands, dispatcher.register(builder))
        }

        fun literal(name: String): LiteralArgument {
            return LiteralArgument(name)
        }

        fun string(name: String): StringArgument {
            return StringArgument(name)
        }

        fun word(name: String): StringArgument {
            return StringArgument(name, StringArgumentType.word())
        }

        fun quotablePhrase(name: String): StringArgument {
            return StringArgument(name, StringArgumentType.string())
        }

        fun greedyPhrase(name: String): StringArgument {
            return StringArgument(name, StringArgumentType.greedyString())
        }

        fun integer(name: String): IntArgument {
            return IntArgument(name)
        }

        fun integer(name: String, min: Int, max: Int): IntArgument {
            return IntArgument(name, min, max)
        }

        fun floatArg(name: String): FloatArgument {
            return FloatArgument(name)
        }

        fun floatArg(name: String, min: Float, max: Float): FloatArgument {
            return FloatArgument(name, min, max)
        }

        fun player(name: String): PlayerArgument {
            return PlayerArgument(name)
        }

        fun uuid(name: String): UUIDArgument {
            return UUIDArgument(name)
        }

        const val SINGLE_SUCCESS: Int = com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
