package zinc.doiche.lib.brigadier.argument.primitive

import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import zinc.doiche.lib.brigadier.argument.RequiredArgument


class FloatArgument @JvmOverloads constructor(
    name: String,
    private val min: Float = Float.MIN_VALUE,
    private val max: Float = Float.MAX_VALUE
) : RequiredArgument<Float>(name) {
    override fun argument(): RequiredArgumentBuilder<CommandSourceStack, Float> {
        return RequiredArgumentBuilder.argument(name, FloatArgumentType.floatArg(min, max))
    }
}
