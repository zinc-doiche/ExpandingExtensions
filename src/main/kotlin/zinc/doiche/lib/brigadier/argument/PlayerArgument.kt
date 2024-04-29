package zinc.doiche.lib.brigadier.argument

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import org.bukkit.entity.Player

class PlayerArgument(
    name: String
) : RequiredArgument<EntitySelector>(name) {
    public override fun argument(): RequiredArgumentBuilder<CommandSourceStack, EntitySelector> {
        return RequiredArgumentBuilder.argument(name, EntityArgument.player())
    }

    companion object {
        @Throws(CommandSyntaxException::class)
        fun getPlayer(source: CommandSourceStack, selector: EntitySelector): Player {
            return selector.findSinglePlayer(source).bukkitEntity
        }

        @Throws(CommandSyntaxException::class)
        fun getPlayer(context: CommandContext<CommandSourceStack>, argument: String): Player {
            return getPlayer(context.source, context.getArgument(argument, EntitySelector::class.java))
        }

        @Throws(CommandSyntaxException::class)
        fun CommandContext<CommandSourceStack>.getPlayer(argument: String) = getPlayer(this, argument)
    }
}
