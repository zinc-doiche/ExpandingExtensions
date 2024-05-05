package zinc.doiche.lib.brigadier

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import zinc.doiche.lib.brigadier.argument.LiteralArgument
import zinc.doiche.lib.brigadier.argument.RequiredArgument
import zinc.doiche.lib.brigadier.command.BiCommand
import zinc.doiche.lib.brigadier.command.PlayerCommand
import java.util.function.BiPredicate
import java.util.function.Predicate
import java.util.function.Supplier

interface CommandNode<T : ArgumentBuilder<CommandSourceStack, T>> {
    fun requires(requires: Predicate<CommandSourceStack>): CommandNode<T>

    fun executes(executes: Command<CommandSourceStack>): CommandNode<T>

    fun <A> then(argument: RequiredArgument<A>): CommandNode<T>

    fun then(argument: LiteralArgument): CommandNode<T>

    fun <A> then(supplier: Supplier<RequiredArgumentBuilder<CommandSourceStack, A>>): CommandNode<T>

    //utility methods
    fun requires(requires: BiPredicate<CommandSourceStack, CommandSender>): CommandNode<T>
    fun requiresOp(): CommandNode<T>
    fun requiresPermission(permission: String): CommandNode<T>
    fun requiresPlayer(): CommandNode<T>
    fun requiresConsole(): CommandNode<T>

    fun executes(executes: BiCommand): CommandNode<T>
    fun executesAsPlayer(executes: (CommandContext<CommandSourceStack>, Player) -> Int): CommandNode<T>
//    fun executesAsPlayer(executes: PlayerCommand): CommandNode<T>
}
