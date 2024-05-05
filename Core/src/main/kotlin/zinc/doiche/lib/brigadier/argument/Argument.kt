package zinc.doiche.lib.brigadier.argument

import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import zinc.doiche.lib.brigadier.CommandNode

interface Argument<T : ArgumentBuilder<CommandSourceStack, T>> : CommandNode<T> {
    fun get(): ArgumentBuilder<CommandSourceStack, T>
}
