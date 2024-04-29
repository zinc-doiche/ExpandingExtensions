package zinc.doiche.lib.brigadier.argument

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.commands.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import zinc.doiche.lib.brigadier.CommandBuilder
import zinc.doiche.lib.brigadier.CommandNode
import zinc.doiche.lib.brigadier.command.BiCommand
import zinc.doiche.lib.brigadier.command.PlayerCommand
import java.util.function.BiPredicate
import java.util.function.Predicate
import java.util.function.Supplier
import kotlin.jvm.Throws

class LiteralArgument(
    protected val name: String
) : Argument<LiteralArgumentBuilder<CommandSourceStack>>, CommandBuilder {
    protected var requires: Predicate<CommandSourceStack>? = null
    protected var executes: Command<CommandSourceStack>? = null
    protected val arguments: MutableList<Argument<*>> = ArrayList()

    override fun <A> then(argument: RequiredArgument<A>): LiteralArgument {
        arguments.add(argument)
        return this
    }

    override fun <A> then(supplier: Supplier<RequiredArgumentBuilder<CommandSourceStack, A>>): LiteralArgument {
        val builder = supplier.get()

        arguments.add(object : RequiredArgument<A>(builder.name) {
            override fun argument(): RequiredArgumentBuilder<CommandSourceStack, A> {
                return builder
            }
        })

        return this
    }

    override fun then(argument: LiteralArgument): LiteralArgument {
        arguments.add(argument)
        return this
    }

    override fun requires(requires: Predicate<CommandSourceStack>): LiteralArgument {
        this.requires = requires
        return this
    }

    override fun requires(requires: BiPredicate<CommandSourceStack, CommandSender>): LiteralArgument {
        return requires { source -> requires.test(source, source.bukkitSender) }
    }

    override fun requiresOp(): LiteralArgument {
        return requires { source -> source.bukkitSender.isOp }
    }

    override fun requiresPermission(permission: String): LiteralArgument {
        return requires { source: CommandSourceStack ->
            source.bukkitSender.hasPermission(permission)
        }
    }

    override fun requiresPlayer(): LiteralArgument {
        return requires { source -> source.isPlayer }
    }

    override fun requiresConsole(): LiteralArgument {
        return requires { source -> !source.isPlayer }
    }

    override fun executes(executes: Command<CommandSourceStack>): LiteralArgument {
        this.executes = executes
        return this
    }

    override fun executes(executes: BiCommand): LiteralArgument {
        return executes { context ->
            executes.run(context, context.source.bukkitSender)
        }
    }

    override fun executesAsPlayer(
        executes: (CommandContext<CommandSourceStack>, Player) -> Int
    ): LiteralArgument = executes { context ->
        executes(context, context.source.bukkitSender as Player)
    }

//    override fun executesAsPlayer(executes: PlayerCommand): LiteralArgument = executes { context ->
//        executes(context, context.source.bukkitSender as Player)
//    }

    override fun get(): LiteralArgumentBuilder<CommandSourceStack> {
        var literal = LiteralArgumentBuilder.literal<CommandSourceStack>(name)

        if (arguments.isNotEmpty()) {
            for (argument in arguments) {
                literal = literal.then(argument.get())
            }
        }
        if (requires != null) {
            literal = literal.requires(requires)
        }
        if (executes != null) {
            literal = literal.executes(executes)
        }

        return literal
    }

    override fun build(): org.bukkit.command.Command {
        return CommandBuilder.command(get())
    }
}
