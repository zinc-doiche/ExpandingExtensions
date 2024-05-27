package zinc.doiche.lib

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.entity.Player
import zinc.doiche.ExpandingExtensions.Companion.plugin

data class CommandHolder(
    val commandBuilder: LiteralCommandNode<CommandSourceStack>,
    val aliases: List<String> = emptyList(),
    val description: String,
) {
    fun register(registrar: Commands) {
        registrar.register(plugin.pluginMeta, commandBuilder, description, aliases)
    }
}

fun simpleCommand(name: String, command: Command<CommandSourceStack>) = Commands
    .literal(name)
    .executes(command)
    .build()

val CommandSourceStack.player: Player?
    get() = this.sender as? Player

fun <T: ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T>.requiresOp(): T {
    return requires { source ->
        source.player?.isOp ?: false
    }
}

fun <T: ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T>.requiresPlayer(): T {
    return requires { source ->
        source.sender is Player
    }
}

fun <T: ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T>.requiresPlayer(
    predicate: (Player) -> Boolean
): T {
    return requires { source ->
        source.player?.let(predicate) ?: false
    }
}

fun <T> RequiredArgumentBuilder<CommandSourceStack, T>.suggestArguments(
    suggest: () -> Collection<String>
): RequiredArgumentBuilder<CommandSourceStack, T> {
    return suggests { _, builder ->
        suggest().forEach { builder.suggest(it) }
        builder.buildFuture()
    }
}

fun <T> RequiredArgumentBuilder<CommandSourceStack, T>.suggestEnums(
    enumClass: Class<out Enum<*>>
): RequiredArgumentBuilder<CommandSourceStack, T> {
    return suggests { _, builder ->
        enumClass.enumConstants.forEach { builder.suggest(it.name) }
        builder.buildFuture()
    }
}

fun <T: ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T>.executesPlayer(
    execute: (CommandContext<CommandSourceStack>, Player) -> Int
): T {
    return executes { context ->
        context.source.player?.let { player ->
            execute(context, player)
        } ?: Command.SINGLE_SUCCESS
    }
}