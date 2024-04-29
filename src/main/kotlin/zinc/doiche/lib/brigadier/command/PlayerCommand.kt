package zinc.doiche.lib.brigadier.command

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.commands.CommandSourceStack
import org.bukkit.entity.Player

fun interface PlayerCommand {
    @Throws(CommandSyntaxException::class)
    fun run(context: CommandContext<CommandSourceStack>, player: Player): Int
}
