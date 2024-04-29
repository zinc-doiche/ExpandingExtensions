package zinc.doiche.lib.brigadier.argument

import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.UuidArgument
import java.util.*

class UUIDArgument(
    name: String
) : RequiredArgument<UUID>(name) {
    override fun argument(): RequiredArgumentBuilder<CommandSourceStack, UUID> {
        return RequiredArgumentBuilder.argument(name, UuidArgument.uuid())
    }
}
