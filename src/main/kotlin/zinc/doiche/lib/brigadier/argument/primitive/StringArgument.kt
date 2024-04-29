package zinc.doiche.lib.brigadier.argument.primitive

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import zinc.doiche.lib.brigadier.argument.RequiredArgument


class StringArgument @JvmOverloads constructor(
    name: String,
    private val argumentType: StringArgumentType = StringArgumentType.word()
) : RequiredArgument<String?>(name) {
    override fun argument(): RequiredArgumentBuilder<CommandSourceStack, String> {
        return RequiredArgumentBuilder.argument(name, argumentType)
    }
}
