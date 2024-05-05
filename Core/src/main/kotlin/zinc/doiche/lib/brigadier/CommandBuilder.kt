package zinc.doiche.lib.brigadier

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.craftbukkit.v1_20_R3.CraftServer
import org.bukkit.craftbukkit.v1_20_R3.command.VanillaCommandWrapper
import org.bukkit.entity.Player
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
            val commands: Commands = minecraftServer.vanillaCommandDispatcher
            val dispatcher: CommandDispatcher<CommandSourceStack> = commands.dispatcher

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
