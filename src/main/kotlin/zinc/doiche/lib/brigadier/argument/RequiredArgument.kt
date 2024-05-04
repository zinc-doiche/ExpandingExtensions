package zinc.doiche.lib.brigadier.argument

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asDeferred
import net.minecraft.commands.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import zinc.doiche.Main.Companion.plugin
import zinc.doiche.lib.brigadier.command.BiCommand
import zinc.doiche.lib.brigadier.command.PlayerCommand
import zinc.doiche.lib.brigadier.command.PlayerSuggestionProvider
import java.util.function.BiPredicate
import java.util.function.Predicate
import java.util.function.Supplier

abstract class RequiredArgument<T>(
    protected val name: String
): Argument<RequiredArgumentBuilder<CommandSourceStack, T>> {
    protected var requires: Predicate<CommandSourceStack>? = null
    protected var executes: Command<CommandSourceStack>? = null
    protected val arguments: MutableList<Argument<*>> = ArrayList()
    protected var suggests: SuggestionProvider<CommandSourceStack>? = null

    protected abstract fun argument(): RequiredArgumentBuilder<CommandSourceStack, T>

    override fun get(): ArgumentBuilder<CommandSourceStack, RequiredArgumentBuilder<CommandSourceStack, T>> {
        var required: RequiredArgumentBuilder<CommandSourceStack, T> = argument()

        if (arguments.isNotEmpty()) {
            for (child in arguments) {
                required = required.then(child.get())
            }
        }
        if (requires != null) {
            required = required.requires(requires)
        }
        if (suggests != null) {
            required = required.suggests(suggests)
        }
        if (executes != null) {
            required = required.executes(executes)
        }

        return required
    }

    override fun requires(requires: Predicate<CommandSourceStack>): RequiredArgument<T> {
        this.requires = requires
        return this
    }

    override fun executes(executes: Command<CommandSourceStack>): RequiredArgument<T> {
        this.executes = executes
        return this
    }

    override fun <A> then(argument: RequiredArgument<A>): RequiredArgument<T> {
        arguments.add(argument)
        return this
    }

    override fun then(argument: LiteralArgument): RequiredArgument<T> {
        arguments.add(argument)
        return this
    }

    override fun <A> then(supplier: Supplier<RequiredArgumentBuilder<CommandSourceStack, A>>): RequiredArgument<T> {
        val builder = supplier.get()

        arguments.add(object : RequiredArgument<A>(builder.name) {
            override fun argument(): RequiredArgumentBuilder<CommandSourceStack, A> {
                return builder
            }
        })

        return this
    }

    fun suggests(suggests: SuggestionProvider<CommandSourceStack>): RequiredArgument<T> {
        this.suggests = suggests
        return this
    }

    fun suggest(argument: String?): RequiredArgument<T> {
        return suggests { _, builder ->
            builder.suggest(argument)
                .buildFuture()
        }
    }

    override fun requires(requires: BiPredicate<CommandSourceStack, CommandSender>): RequiredArgument<T> {
        return requires { source -> requires.test(source, source.bukkitSender) }
    }

    override fun requiresOp(): RequiredArgument<T> {
        return requires { source -> source.bukkitSender.isOp }
    }

    override fun requiresPermission(permission: String): RequiredArgument<T> {
        return requires { source: CommandSourceStack ->
            source.bukkitSender.hasPermission(permission)
        }
    }

    override fun requiresPlayer(): RequiredArgument<T> {
        return requires { obj: CommandSourceStack -> obj.isPlayer }
    }

    override fun requiresConsole(): RequiredArgument<T> {
        return requires { source: CommandSourceStack -> !source.isPlayer }
    }

    override fun executes(executes: BiCommand): RequiredArgument<T> {
        return executes { context ->
            executes.run(context, context.source.bukkitSender)
        }
    }

    override fun executesAsPlayer(
        executes: (CommandContext<CommandSourceStack>, Player) -> Int
    ): RequiredArgument<T> = executes { context ->
        executes(context, context.source.bukkitSender as Player)
    }

//    override fun executesAsPlayer(executes: PlayerCommand): RequiredArgument<T> = executes { context ->
//        executes(context, context.source.bukkitSender as Player)
//    }

    fun suggestsWithPlayer(provider: PlayerSuggestionProvider): RequiredArgument<T> {
        return suggests { context, builder ->
            provider.getSuggestions(context.source.bukkitSender as Player, builder)
        }
    }

    fun suggestArguments(vararg arguments: String): RequiredArgument<T> {
        return suggests { _, builder ->
            for (argument in arguments) {
                builder.suggest(argument)
            }
            builder.buildFuture()
        }
    }

    /**
     * 단순 [Collection]을 사용하면 초기 등록 이후의 [Collection]의 요소 변경을 반영하지 못하고
     *
     * 처음 등록된 요소만 제안해주므로 [Supplier] 사용
     * @param arguments 제안할 요소의 [Supplier]
     * @return [RequiredArgument]
     */
    fun suggestArguments(arguments: Supplier<Collection<String>>): RequiredArgument<T> {
        return suggests { _, builder ->
            val strings = arguments.get()
            for (argument in strings) {
                builder.suggest(argument)
            }
            builder.buildFuture()
        }
    }

    /**
     * @param enums adjust with `Enum.values()`
     */
    fun suggestEnums(enums: Array<Enum<*>>): RequiredArgument<T> {
        return suggests { _, builder ->
            for (argument in enums) {
                builder.suggest(argument.name)
            }
            builder.buildFuture()
        }
    }
}
